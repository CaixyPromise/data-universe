package com.caixy.backend.bizmq;

import com.caixy.backend.model.enums.MessageQueueConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AnalysisMessageProducer
{

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送
     *
     * @param message
     */
    public void sendMessage(String message)
    {
        rabbitTemplate.convertAndSend(MessageQueueConstant.ANALYSIS_EXCHANGE, MessageQueueConstant.ANALYSIS_ROUTE_KEY, message);
    }

}
