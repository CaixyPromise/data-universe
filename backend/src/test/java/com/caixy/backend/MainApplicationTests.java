package com.caixy.backend;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.KeyCredential;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.config.AzureOpenAiConfig;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.model.dto.chat.InputPrompt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 主类测试
 */
@SpringBootTest
@Slf4j
class MainApplicationTests
{
    @Resource
    private AzureOpenAiConfig azureOpenAiConfig;

    @Test
    void tryOpenAI()
    {
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(azureOpenAiConfig.getEndPoint())
                .credential(new KeyCredential(azureOpenAiConfig.getApiKey()))
                .buildClient();
        String deploymentOrModelId = azureOpenAiConfig.getModelId();

        InputPrompt inputPrompt = new InputPrompt();
        inputPrompt.setChartType("柱状图");
        inputPrompt.setTarget("价格分析");
        inputPrompt.setData("标题\t房子位置\t房屋类型\t关注信息\t价格（万）\t单价（元/平）\t面积（平米）\t室\t厅\t关注人数\t发布时间\n" +
                "龙光玖龙府大花园小区 朝向好 三房两卫\t龙光玖龙府 \t3室2厅;91平米;东南;精装;中楼层(共30层);平房\t0人关注 / 8天以前发布\t96\t10550\t91\t3\t2\t0\t8天以前\n" +
                "中楼层，精装2房2厅2卫卖小见小\t勒流\t2室2厅;85.62平米;南;其他;中楼层(共6层);板塔结合\t0人关注 / 5天以前发布\t65\t7592\t85.62\t2\t2\t0\t5天以前\n" +
                "恒大山水龙盘精装没住过 大三房南北对流\t江滨花园 \t3室2厅;121.34平米;东南;精装;中楼层(共32层);暂无数据\t7人关注 / 2个月以前发布\t83\t6841\t121.34\t3\t2\t7\t2个月以前\n" +
                "红星社区 4室2厅 东南\t西樵\t4室2厅;238.27平米;东南;精装;低楼层(共3层);暂无数据\t0人关注 / 9天以前发布\t298\t12507\t238.27\t4\t2\t0\t9天以前\n" +
                "万科精装3房，出售啦，看房方便，采光好。\t恒大山水龙盘 \t3室2厅;91.28平米;南;精装;20层;塔楼\t1人关注 / 3个月以前发布\t108\t11832\t91.28\t3\t2\t1\t3个月以前\n" +
                "南向实用精装4房  全新未住 采光充足\t丹灶\t4室1厅;108平米;南;精装;中楼层(共32层);塔楼\t0人关注 / 7天以前发布\t83\t7686\t108\t4\t1\t0\t7天以前\n" +
                "小区环境优美，配套成熟。采光视野佳。近地铁\t红星社区 \t3室1厅;89.66平米;东北;精装;低楼层(共25层);2016年;塔楼\t0人关注 / 7天以前发布\t150\t16730\t89.66\t3\t1\t0\t7天以前\n" +
                "过五年唯一 南向 3房 有钥匙 方便看房\t容桂\t3室1厅;95平米;南;简装;中楼层(共8层);板楼\t4人关注 / 11天以前发布\t80\t8422\t95\t3\t1\t4\t11天以前\n" +
                "此房满二 近新动力广场 近地铁口\t万科城市之光 \t4室1厅;104.51平米;南;精装;高楼层(共29层);塔楼\t1人关注 / 8天以前发布\t79\t7560\t104.51\t4\t1\t1\t8天以前\n");

        List<ChatRequestMessage> chatMessages = inputPrompt.buildChatRequestMessage();
        //建立连接
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        log.info("chatCompletionsOptions.getMessages(): {}", chatCompletionsOptions.getMessages());
        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String result = (chatCompletions.getChoices().get(0).getMessage().getContent());
        System.out.println("result: " + result);
        String[] splits = result.split("【【【【【");
        if (splits.length < 3)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        System.out.println("genChart: " + genChart + "\ngenResult: " + genResult);

        CompletionsUsage usage = chatCompletions.getUsage();

        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }

}
