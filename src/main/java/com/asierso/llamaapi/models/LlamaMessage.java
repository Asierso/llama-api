package com.asierso.llamaapi.models;

public class LlamaMessage {
    private String role;
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LlamaMessage(String role,String content) {
        this.role = role;
        this.content = content;
    }

    public LlamaMessage(){

    }
}
