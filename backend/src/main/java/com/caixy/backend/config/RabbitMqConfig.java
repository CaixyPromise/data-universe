package com.caixy.backend.config;

import com.caixy.backend.model.enums.MessageQueueConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列初始化配置
 *
 * @name: com.caixy.backend.config.RabbitConfig
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 14:33
 **/
@Configuration
public class RabbitMqConfig
{
    // 创建队列
    @Bean
    Queue analysisQueue()
    {
        return new Queue(MessageQueueConstant.ANALYSIS_QUEUE, true);
    }

    // 创建交换机
    @Bean
    DirectExchange analysisExchange()
    {
        return new DirectExchange(MessageQueueConstant.ANALYSIS_EXCHANGE);
    }

    // 绑定队列和交换机
    @Bean
    Binding binding(Queue analysisQueue, DirectExchange analysisExchange)
    {
        return BindingBuilder.bind(analysisQueue).to(analysisExchange).with(MessageQueueConstant.ANALYSIS_ROUTE_KEY);
    }

    // RabbitAdmin自动声明组件
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory)
    {
        return new RabbitAdmin(connectionFactory);
    }
}
