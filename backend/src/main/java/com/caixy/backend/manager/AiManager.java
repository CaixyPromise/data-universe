package com.caixy.backend.manager;

import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.model.dto.chat.InputPrompt;
import com.caixy.backend.utils.AzureOpenAIClient;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager
{

    @Resource
    private YuCongMingClient yuCongMingClient;

    @Resource
    private AzureOpenAIClient azureOpenAIClient;

    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    public String doChat(long modelId, String message)
    {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }

    public String doChatByAzure(InputPrompt inputPrompt)
    {
        return azureOpenAIClient.send(inputPrompt);
    }
}
