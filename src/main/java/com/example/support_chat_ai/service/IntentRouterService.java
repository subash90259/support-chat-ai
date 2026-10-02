package com.example.support_chat_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IntentRouterService {

    private final ChatClient chatClient;

    public IntentRouterService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String detectIntent(String userMessage) {

        String result = chatClient
                .prompt()
                .system("""
        You are ONLY an intent classifier.

        Your job is to classify the user's message into
        exactly ONE category:

        NORMAL
        FILESYSTEM
        BROWSER

        =========================
        NORMAL
        =========================

        NORMAL means the user wants an answer, explanation,
        code, programming help, or general conversation.

        These MUST be NORMAL:

        "What is Java?"
        "What is OOP?"
        "Explain inheritance"
        "What is StringBuilder?"
        "Write a Java program"
        "Write a Java program to reverse a String"
        "Find duplicate elements in an array"
        "Find second largest number"
        "What is Spring Boot?"
        "Explain JWT"
        "What is Angular?"
        "How does REST API work?"
        "Solve this coding problem"
        "Help me with Java"
        "Explain this code"

        If the user asks a QUESTION, it is NORMAL
        unless the question explicitly asks to open
        a website or perform a filesystem action.

        Programming questions are ALWAYS NORMAL.

        Technical questions are ALWAYS NORMAL.

        General knowledge questions are ALWAYS NORMAL.

        =========================
        FILESYSTEM
        =========================

        FILESYSTEM means the user explicitly wants
        an operation on a local file or folder.

        Examples:

        "Does Downloads exist?"
        "Open Downloads"
        "Open Documents"
        "Find Java folder"
        "Copy Java folder to Documents"
        "Close Downloads"

        =========================
        BROWSER
        =========================

        BROWSER means ONLY this:

        The user explicitly wants to OPEN, LAUNCH,
        or GO TO a website.

        Examples:

        "Open YouTube"
        "Launch YouTube"
        "Open Google"
        "Go to GitHub"
        "Open ChatGPT"
        "YouTube open pannu"

        =========================
        VERY IMPORTANT
        =========================

        The word "open" alone does NOT automatically mean BROWSER.

        "How do I open a file in Java?"
        = NORMAL

        "How do I open Chrome using Java?"
        = NORMAL

        "What is Java?"
        = NORMAL

        "What is YouTube?"
        = NORMAL

        "How does YouTube work?"
        = NORMAL

        "Open YouTube"
        = BROWSER

        "YouTube open pannu"
        = BROWSER

        "Open Downloads"
        = FILESYSTEM

        "Does Downloads folder exist?"
        = FILESYSTEM

        Never classify a programming question as BROWSER.

        Never classify a normal question as BROWSER.

        Never classify a technical explanation as BROWSER.

        Return ONLY one word:

        NORMAL

        FILESYSTEM

        BROWSER
        """)
                .user(userMessage)
                .call()
                .content();

        return result.trim().toUpperCase();
    }
}