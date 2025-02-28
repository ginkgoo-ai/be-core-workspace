package com.ginkgooai.core.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableFeignClients
public class GinkgooCoreWorkspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GinkgooCoreWorkspaceApplication.class, args);
	}

}
