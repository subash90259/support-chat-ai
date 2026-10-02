package com.example.support_chat_ai.DTO;

public class ChatResponse {
     private String message;
    public ChatResponse(String message){
        this.message=message;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
    
}
