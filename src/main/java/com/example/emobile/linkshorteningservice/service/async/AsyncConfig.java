package com.example.emobile.linkshorteningservice.service.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final AsyncProperties asyncProperties;

    @Bean
    public Executor taskExecutor() {
        var threadPoolExecutor = new ThreadPoolTaskExecutor();

        threadPoolExecutor.setCorePoolSize(asyncProperties.getCorePoolSize());
        threadPoolExecutor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        threadPoolExecutor.setQueueCapacity(asyncProperties.getQueueCapacity());
        threadPoolExecutor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        threadPoolExecutor.initialize();

        return threadPoolExecutor;
    }
}