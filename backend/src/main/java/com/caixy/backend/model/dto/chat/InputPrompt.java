package com.caixy.backend.model.dto.chat;

import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.caixy.backend.model.entity.Chart;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入提示词封装类
 *
 * @name: com.caixy.backend.model.dto.chat.InputPrompt
 * @author: CAIXYPROMISE
 * @since: 2024-04-24 14:55
 **/
@Data
public class InputPrompt
{
    /**
     * 输出图表类型
     */
    private String chartType;
    /**
     * 分析姆比奥
     */
    private String target;
    /**
     * 数据
     */
    private String data;

    public String buildPrompt()
    {
        return "身份确认：您好，我希望你作为一个数据分析与可视化专家！我希望能借助您的专业技能来解决。" +
                "\n\n" + "数据说明：我现在有一份这样的数据\n" +
                this.data + "\n\n" + "分析目标：我的主要分析目标是" +
                this.target + "\n\n" + "可视化需求：为了更好地呈现这些分析结果，我希望通过"
                + this.chartType + "来清晰展现数据之间的关系。\n\n" +
                "特别要求：完成数据分析后，可视化请提供相应的" + this.chartType +
                "的Echarts的JavaScript-option选项代码，以便我能够直接使用Echarts来实现这些图表的可视化。" +
                "输出格式: " + "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                "{开头引入介绍，不要做太多解释}"+
                "【【【【【\n" +
                "{这里不需要添加任何文字和描述，这里只可以直接生成前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\\n" +
                "【【【【【\n" + "{明确的数据分析结论、越详细越好，不要生成多余的注释，也不是解释上面的echarts代码}" +
                "在编写代码时，请注意不需要添加注释。";
    }

    public static InputPrompt build(Chart chart)
    {
        InputPrompt inputPrompt = new InputPrompt();
        inputPrompt.setData(chart.getChartData());
        inputPrompt.setTarget(chart.getGoal());
        inputPrompt.setChartType(chart.getChartType());
        return inputPrompt;
    }

    public List<ChatRequestMessage> buildChatRequestMessage()
    {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();

        chatMessages.add(new ChatRequestSystemMessage("身份确认：您好，我希望你作为一个数据分析与可视化专家！我希望能借助您的专业技能来解决。"));
        chatMessages.add(new ChatRequestSystemMessage("回复语言确认：请使用中文回答我的问题"));
        chatMessages.add(new ChatRequestSystemMessage("工作确认： " +
                "我需要你进行数据分析与可视化工作，首先我会给你对应的数据，你需要按照我的要求进行数据分析，" +
                "然后我会给出一个可视化要求结果，你需要帮我分析这个数据，并根据我想要的可视化图表的内容返回echarts" +
                "来实现图表可视化" +
                "。"));
        chatMessages.add(new ChatRequestSystemMessage("输出格式: " +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释、代码块格式）。【【【【【是你需要输出的内容，输出时需要带上【【【【【"));
        chatMessages.add(new ChatRequestSystemMessage("{开头引入介绍，不要做太多解释}"));
        chatMessages.add(new ChatRequestSystemMessage("【【【【【\n"));
        chatMessages.add(new ChatRequestSystemMessage("{这里不需要添加任何文字和描述，这里只可以直接生成前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}"));
        chatMessages.add(new ChatRequestSystemMessage("【【【【【\n"));
        chatMessages.add(new ChatRequestSystemMessage("{明确的数据分析结论、越详细越好，不要生成多余的注释，也不是解释上面的echarts代码}"));
        chatMessages.add(new ChatRequestSystemMessage("【【【【【\n"));

        chatMessages.add(new ChatRequestSystemMessage("在编写代码时，请注意不需要添加注释。"));
        chatMessages.add(new ChatRequestSystemMessage("下面是用户的数据和要求"));


        chatMessages.add(new ChatRequestUserMessage("用户的主要分析目标是" + target + "\n"));
        chatMessages.add(new ChatRequestUserMessage("可视化需求：为了更好地呈现这些分析结果，我希望通过" + chartType + "来清晰展现数据之间的关系。\n"));
        chatMessages.add(new ChatRequestUserMessage("特别要求：完成数据分析后，可视化请提供相应的【" + chartType + "】的Echarts的JavaScript-option选项代码，以便我能够直接使用Echarts来实现这些图表的可视化。\n"));
        chatMessages.add(new ChatRequestUserMessage("用户的数据如下：\n" + data + "\n\n"));
        chatMessages.add(new ChatRequestUserMessage("请按照以上要求，帮我完成数据分析，并生成可视化图表。"));
        return chatMessages;
    }
}
