package com.asierso.llamaapi.builder;

import com.asierso.llamaapi.handlers.LlamaRequestBase;
import com.asierso.llamaapi.models.LlamaRequest;

public class LlamaPromptsBuilder {
    private final LlamaRequest req;
    private final StringBuilder prompt;
    public LlamaPromptsBuilder(LlamaRequestBase data){
        this.req = new LlamaRequest(data);
        this.prompt = new StringBuilder();
    }

    public LlamaPromptsBuilder appendPrompt(String content){
        prompt.append(content).append("\n");
        return this;
    }

    public LlamaRequest build() {
        req.setPrompt(prompt.toString());
        return req;
    }
}
