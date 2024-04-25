package com.caixy.backend.utils;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.caixy.backend.config.AzureOpenAiConfig;
import com.caixy.backend.model.dto.chat.InputPrompt;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Ai工具集
 *
 * @name: com.caixy.backend.utils.AzureOpenAIClient
 * @author: CAIXYPROMISE
 * @since: 2024-04-24 12:49
 **/
@Slf4j
@Component
public class AzureOpenAIClient
{
    private final AzureOpenAiConfig azureOpenAiConfig;

    private final OpenAIClient client;

    @Autowired
    public AzureOpenAIClient(AzureOpenAiConfig azureOpenAiConfig)
    {
        this.azureOpenAiConfig = azureOpenAiConfig;
        this.client = azureOpenAiConfig.getOpenAIClient();
    }


    /**
     * 聊天
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/24 21:49
     */
    public String send(InputPrompt inputPrompt)
    {
        List<ChatRequestMessage> chatMessages = inputPrompt.buildChatRequestMessage();
        //建立连接
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        ChatCompletions chatCompletions =
                client.getChatCompletions(azureOpenAiConfig.getModelId(), new ChatCompletionsOptions(chatMessages));
        return (chatCompletions.getChoices().get(0).getMessage().getContent());
    }
}
