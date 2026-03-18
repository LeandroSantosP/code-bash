package com.leandrosps.bug_bash.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackages = "com.leandrosps.bug_bash.app")
public class GloblaConfig {
}
