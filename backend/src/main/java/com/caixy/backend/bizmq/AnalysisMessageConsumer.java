package com.caixy.backend.bizmq;

import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.manager.AiManager;
import com.caixy.backend.model.dto.chat.InputPrompt;
import com.caixy.backend.model.entity.Chart;
import com.caixy.backend.model.enums.ChartStatusEnum;
import com.caixy.backend.model.enums.MessageQueueConstant;
import com.caixy.backend.service.ChartService;
import com.caixy.backend.utils.JsonUtils;
import com.caixy.backend.utils.RegexUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class AnalysisMessageConsumer
{

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {MessageQueueConstant.ANALYSIS_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
    {
        log.info("receiveMessage message = {}", message);
        if (StringUtils.isBlank(message))
        {
            // 如果失败，消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        Long chartId = Long.valueOf(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null)
        {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatusEnum.RUNNING.getValue());
        boolean b = chartService.updateById(updateChart);
        if (!b)
        {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败", "");
            return;
        }
        // 构建输入Prompt
        InputPrompt inputPrompt = InputPrompt.build(chart);
        // 调用 AI
        log.info("调用Azure Ai");
        String result = aiManager.doChatByAzure(inputPrompt);
        String[] splits = result.split("【【【【【");
        if (splits.length < 3)
        {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误", result);
            return;
        }
        Chart updateChartResult = getChartResult(splits, chart);
        updateChartResult.setResponseContent(result);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult)
        {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败", result);
        }
        // 消息确认
        channel.basicAck(deliveryTag, false);
    }

    private static @NotNull Chart getChartResult(String[] splits, Chart chart)
    {
        // 提取json字符串（单纯取出{}的内容）
        String extraJson = RegexUtils.extraJson(splits[1].trim());
        // 修复或转化成标准json字符串并返回
        String genChart = JsonUtils.fixedJson(extraJson);
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);

        updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
        return updateChartResult;
    }

    /**
     * 构建用户输入
     *
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart)
    {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType))
        {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

    private void handleChartUpdateError(long chartId, String execMessage, String responseContent)
    {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStatusEnum.FAIL.getValue());
        updateChartResult.setExecMessage(execMessage);
        updateChartResult.setResponseContent(responseContent);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult)
        {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

}
