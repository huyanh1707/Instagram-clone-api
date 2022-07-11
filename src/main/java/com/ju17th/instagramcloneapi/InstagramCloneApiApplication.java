package com.ju17th.instagramcloneapi;

import com.ju17th.instagramcloneapi.utils.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class InstagramCloneApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(InstagramCloneApiApplication.class, args);
    }
}
