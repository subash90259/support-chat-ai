package com.example.support_chat_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class SupportChatAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportChatAiApplication.class, args);
		 Application.launch(DesktopApp.class, args);
	}

}
