package com.example.support_chat_ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.support_chat_ai.DTO.ChatRequest;
import com.example.support_chat_ai.DTO.ChatResponse;
import com.example.support_chat_ai.tool.BrowserTool;
import com.example.support_chat_ai.tool.FileSystemTool;

@Service
public class ChatService {

    private final FileSystemTool fileSystemTool;
    private final BrowserTool browserTool;

    private final ChatClient normalChatClient;
    private final ChatClient toolChatClient;

    private final IntentRouterService intentRouterService;

    @Value("${assistant.browser.enabled:false}")
    private boolean browserEnabled;

    public ChatService(
            ChatClient.Builder builder,
            FileSystemTool fileSystemTool,
            BrowserTool browserTool,
            IntentRouterService intentRouterService) {

        this.fileSystemTool = fileSystemTool;
        this.browserTool = browserTool;
        this.intentRouterService = intentRouterService;

        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .maxMessages(20)
                .build();

        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build();

        // Normal AI - NO TOOLS
        this.normalChatClient = builder
                .defaultAdvisors(memoryAdvisor)
                .build();

        // Tool-enabled AI
        this.toolChatClient = builder
                .defaultAdvisors(memoryAdvisor)
                .build();
    }

    public ChatResponse chat(ChatRequest request) {

        String message = request.getMessage();

        System.out.println();
        System.out.println("====================================");
        System.out.println("USER MESSAGE = " + message);

        long totalStart = System.currentTimeMillis();

        // ==========================================
        // STEP 1: AI determines intent
        // ==========================================

        long intentStart = System.currentTimeMillis();

        String intent = intentRouterService.detectIntent(message);

        long intentEnd = System.currentTimeMillis();

        System.out.println("DETECTED INTENT = " + intent);
        System.out.println(
                "INTENT DETECTION TIME = "
                        + (intentEnd - intentStart)
                        + " ms"
        );

        String conversationId = "desktop-user";

        String reply;

        // ==========================================
        // STEP 2: Route based on intent
        // ==========================================

        long answerStart = System.currentTimeMillis();

        switch (intent) {

            // ==========================================
            // BROWSER
            // ==========================================

            case "BROWSER":

                System.out.println("BROWSER REQUEST");

                if (!browserEnabled) {

                    System.out.println("BrowserTool is DISABLED");

                    reply = """
                            Browser control is currently disabled.
                            Your current browser pages will not be changed.
                            """;

                    break;
                }

                System.out.println("ROUTING TO BROWSER TOOL");

                reply = toolChatClient
                        .prompt()
                        .system("""
                                You are a Windows desktop assistant.

                                The user explicitly wants to open
                                or launch a website.

                                Use BrowserTool to perform the
                                requested website opening.

                                Do not provide unnecessary explanations.
                                """)
                        .user(message)
                        .advisors(advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        ))
                        .tools(browserTool)
                        .call()
                        .content();

                break;

            // ==========================================
            // FILESYSTEM
            // ==========================================

            case "FILESYSTEM":

                System.out.println("ROUTING TO FILESYSTEM TOOL");

                reply = toolChatClient
                        .prompt()
                        .system("""
                                You are a Windows desktop assistant.

                                The user wants to perform an operation
                                involving a local file or folder.

                                Use FileSystemTool to perform the
                                requested operation.

                                Follow the available filesystem
                                tool descriptions carefully.

                                Do not provide unnecessary explanations.
                                """)
                        .user(message)
                        .advisors(advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        ))
                        .tools(fileSystemTool)
                        .call()
                        .content();

                break;

            // ==========================================
            // NORMAL
            // ==========================================

            case "NORMAL":

                System.out.println("ROUTING TO NORMAL AI");

                reply = normalChatClient
                        .prompt()
                        .system("""
                                You are a helpful and reliable AI assistant.

                                ==========================================
                                GENERAL RULES
                                ==========================================

                                - Answer only the user's question.
                                - Do not add unrelated statements.
                                - Do not invent facts.
                                - Do not make up names, dates, definitions,
                                  or historical information.
                                - If you are unsure about something,
                                  clearly say that you are unsure.
                                - Keep answers clear and relevant.

                                ==========================================
                                JAVA / PROGRAMMING
                                ==========================================

                                When the user says "Java", they normally mean
                                the Java programming language and software
                                development, unless the user clearly says
                                they mean Javanese language, culture, or
                                Indonesia.

                                Examples:

                                "What is Java?"
                                -> Explain the Java programming language.

                                "What is Java OOP?"
                                -> Explain OOP in Java.

                                "What is a Java class?"
                                -> Explain Java classes.

                                "Write a Java program"
                                -> Provide Java code.

                                "Explain Spring Boot"
                                -> Explain Spring Boot.

                                For programming questions:
                                - Give technically correct answers.
                                - Use Markdown code blocks when code is required.
                                - Give practical examples when useful.
                                - Do not invent APIs, classes, methods, or syntax.

                                ==========================================
                                MULTIPLE CHOICE QUESTIONS
                                ==========================================

                                When the user asks a multiple-choice question:

                                - Identify the correct option.
                                - Carefully calculate or reason before answering.
                                - Return the option exactly as provided.
                                - Do not change the question or options.
                                - Do not invent another option.
                                - Give a short explanation after the answer.

                                Example:

                                Question:
                                What is the output?

                                int[] arr = {5, 10, 15, 20};
                                int sum = 0;

                                for (int i = 0; i < arr.length; i++) {
                                    sum = sum + arr[i];
                                }

                                Options:
                                A) 40
                                B) 45
                                C) 50
                                D) 55

                                Correct response:

                                C) 50

                                Explanation:
                                5 + 10 + 15 + 20 = 50.

                                ==========================================
                                LANGUAGE
                                ==========================================

                                Respond in the user's language.

                                If the user uses English,
                                answer in English.

                                If the user uses Tamil or Tanglish,
                                answer in Tamil/Tanglish.

                                ==========================================
                                TOOLS
                                ==========================================

                                Do not browse the internet.

                                Do not open websites.

                                Do not use computer tools.

                                You are answering the user's question
                                directly.
                                """)
                        .user(message)
                        .advisors(advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        ))
                        .call()
                        .content();

                break;

            // ==========================================
            // UNKNOWN
            // ==========================================

            default:

                System.out.println("UNKNOWN INTENT -> NORMAL AI");

                reply = normalChatClient
                        .prompt()
                        .system("""
                                You are a helpful AI assistant.

                                Answer only the user's question.

                                Do not add unrelated statements.

                                If the question is about programming,
                                provide technically correct programming
                                information.

                                If it is a multiple-choice question,
                                identify the correct option and briefly
                                explain why.

                                If you are unsure, say so instead of
                                inventing information.
                                """)
                        .user(message)
                        .advisors(advisor -> advisor.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        ))
                        .call()
                        .content();

                break;
        }

        long answerEnd = System.currentTimeMillis();

        // ==========================================
        // TIMING
        // ==========================================

        System.out.println(
                "ANSWER GENERATION TIME = "
                        + (answerEnd - answerStart)
                        + " ms"
        );

        System.out.println(
                "TOTAL RESPONSE TIME = "
                        + (answerEnd - totalStart)
                        + " ms"
        );

        System.out.println("AI RESPONSE = " + reply);

        System.out.println("====================================");

        return new ChatResponse(reply);
    }
}