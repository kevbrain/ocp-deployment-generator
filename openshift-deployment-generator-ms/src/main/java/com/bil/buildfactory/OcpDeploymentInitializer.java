package com.bil.buildfactory;


import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OcpDeploymentInitializer {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OcpDeploymentInitializer.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
        app.run();
		 SpringApplication.run(OcpDeploymentInitializer.class, args);

	}
	
}
