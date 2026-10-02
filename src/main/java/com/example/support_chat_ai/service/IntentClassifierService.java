package com.example.support_chat_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IntentClassifierService {

    private final ChatClient classifierClient;

    public IntentClassifierService(ChatClient.Builder builder) {
        this.classifierClient = builder.build();
    }

    public String classify(String message) {

        String result = classifierClient
                .prompt()
                .system("""
                        You are an intent classifier for a desktop AI assistant.

                        Understand the user's meaning, not keywords.

                        Return ONLY one of these values:

                        NORMAL
                        FILESYSTEM
                        BROWSER

                        NORMAL:
                        General questions, programming questions,
                        coding requests, explanations and normal conversation.

                        FILESYSTEM:
                        Any operation involving files or folders
                        on the user's computer.

                        BROWSER:
                        Explicitly opening or launching a website.

                        Examples:

                        "What is Java?" -> NORMAL

                        "Write a Java program to reverse a String"
                        -> NORMAL

                        "Explain Spring Boot"
                        -> NORMAL

                        "Downloads folder iruka?"
                        -> FILESYSTEM

                        "Open Documents folder"
                        -> FILESYSTEM

                        "Copy Java folder to Documents"
                        -> FILESYSTEM

                        "Open YouTube"
                        -> BROWSER

                        "Open ChatGPT"
                        -> BROWSER

                        "Google-ku po"
                        -> BROWSER
                        """)
                .user(message)
                .call()
                .content();

        return result.trim().toUpperCase();
    }
}