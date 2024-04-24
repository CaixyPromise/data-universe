package com.caixy.backend.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.policy.RetryStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * AzureAi配置
 *
 * @name: com.caixy.backend.config.AzureOpenAiConfig
 * @author: CAIXYPROMISE
 * @since: 2024-04-23 21:59
 **/
@Slf4j
@Configuration
@ConfigurationProperties("azure.openai")
@Data
public class AzureOpenAiConfig
{
    private String apiKey;
    private String spareKey;
    private String endPoint;
    private String modelId;

    /**
     * 获取OpenAIClient
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/24 下午2:53
     */
    public OpenAIClient getOpenAIClient()
    {
        RetryPolicy retryPolicy = new RetryPolicy(new RetryStrategy() {
            @Override
            public int getMaxRetries() {
                return 3; // 最大重试次数为3
            }

            @Override
            public Duration calculateRetryDelay(int i)
            {
                return Duration.ofSeconds(3);
            }
        });
        return new OpenAIClientBuilder()
                .endpoint(endPoint)
                .credential(new KeyCredential(apiKey))
                .httpClient(new NettyAsyncHttpClientBuilder().connectTimeout(Duration.ofSeconds(60)).build())
                .retryPolicy(retryPolicy)
                .buildClient();
    }
}
