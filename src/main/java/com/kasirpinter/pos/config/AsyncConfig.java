package com.kasirpinter.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Minimum threads aktif
        executor.setMaxPoolSize(50); // Maksimum threads
        executor.setQueueCapacity(200); // Antrean tugas
        executor.setKeepAliveSeconds(60); // Waktu hidup idle threads
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // Tangani tugas yang ditolak
        executor.initialize();
        return executor;
    }


}
