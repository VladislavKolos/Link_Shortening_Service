package com.example.emobile.linkshorteningservice.service.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
    public static final int MAX_POOL_SIZE = 20;
    public static final int CORE_POOL_SIZE = 5;
    public static final int QUEUE_CAPACITY = 100;
    public static final String THREAD_NAME_PREFIX = "AsyncClickCounter-";

    @Bean
    public Executor taskExecutor() {
        var threadPoolExecutor = new ThreadPoolTaskExecutor();

        threadPoolExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            if (r instanceof Runnable runnable) {
                runnable.run();
            }
        });

        threadPoolExecutor.initialize();

        return threadPoolExecutor;
    }
}