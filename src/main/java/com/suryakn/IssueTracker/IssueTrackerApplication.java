package com.suryakn.IssueTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // for created and modified
@EnableCaching // enable Redis caching
@ComponentScan(basePackages = {"com.suryakn.IssueTracker"})
public class IssueTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(IssueTrackerApplication.class, args);
    }
}
