package com.example.demo.config;

import com.backblaze.b2.client.B2Client;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.example.demo.property.BackblazeB2Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackblazeB2Config {

    @Bean
    public B2Client b2StorageClient(BackblazeB2Properties properties) {
        return B2StorageClientFactory.builder(
                properties.getAccountId(),
                properties.getApplicationKey()
        ).build();
    }
}