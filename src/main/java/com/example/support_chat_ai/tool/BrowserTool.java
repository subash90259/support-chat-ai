package com.example.support_chat_ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.example.support_chat_ai.service.ChatService;

import java.awt.Desktop;
import java.net.URI;
@Component 
public class BrowserTool {
@Tool(description = """
        This tool ONLY opens a website in the user's browser.

        Use this tool ONLY when the user explicitly asks:
        - Open a website
        - Launch a website
        - Go to a website

        Examples:
        Open YouTube
        Open Google
        Open ChatGPT
        Launch GitHub
        Go to Claude
        YouTube open pannu

        DO NOT use this tool for:
        - Java questions
        - Programming questions
        - Writing Java code
        - Explaining Java
        - Spring Boot questions
        - Angular questions
        - Coding problems
        - Technical explanations
        - General questions
        - Searching for answers

        IMPORTANT:
        "Write a Java program to reverse a String"
        MUST NOT call this tool.

        "Explain StringBuilder"
        MUST NOT call this tool.

        "How to reverse a String in Java?"
        MUST NOT call this tool.

        "Write Selenium code"
        MUST NOT call this tool.

        The tool should only be called when opening a website
        is explicitly the user's requested action.
        """)
public String openWebsite(String website) {
    
    System.out.println("========== BROWSER TOOL CALLED ==========");
    System.out.println("WEBSITE = " + website);

    try {
        String url = convertToUrl(website);

        System.out.println("URL = " + url);

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "",
                        url
                );

        processBuilder.start();

        System.out.println("BROWSER OPEN COMMAND SENT");

        return "WEBSITE_OPENED_SUCCESSFULLY: " + url;

    } catch (Exception e) {

        e.printStackTrace();

        return "FAILED_TO_OPEN_WEBSITE: " + e.getMessage();
    }
}

    private String convertToUrl(String website) {

        String value = website.trim();

        if (value.startsWith("http://")
                || value.startsWith("https://")) {

            return value;
        }

        if (value.equalsIgnoreCase("youtube")) {
            return "https://www.youtube.com";
        }

        if (value.equalsIgnoreCase("chatgpt")) {
            return "https://chatgpt.com";
        }

        if (value.equalsIgnoreCase("claude")) {
            return "https://claude.ai";
        }

        if (value.equalsIgnoreCase("google")) {
            return "https://www.google.com";
        }

        if (value.equalsIgnoreCase("github")) {
            return "https://github.com";
        }

        if (value.equalsIgnoreCase("linkedin")) {
            return "https://www.linkedin.com";
        }

        if (!value.contains(".")) {
            return "https://www.google.com/search?q="
                    + value.replace(" ", "+");
        }

        return "https://" + value;
    }

}
