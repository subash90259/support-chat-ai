package com.example.support_chat_ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.support_chat_ai.DTO.ChatRequest;
import com.example.support_chat_ai.DTO.ChatResponse;
import com.example.support_chat_ai.service.ChatService;



@RestController 
@RequestMapping ("/api/chat")
public class ChatController {
    @Autowired 
    private ChatService chatService;
    public ChatController(ChatService chatService)
    {
        this.chatService=chatService;
    }
    @PostMapping ("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request)
    {
        return chatService.chat(request);
    }
}
