package com.leandrosps.bug_bash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.leandrosps.bug_bash.app.HttpClient;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		HttpClient.getInstance().healthCheck();
	}

}
