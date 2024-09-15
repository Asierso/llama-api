package com.asierso.llamaapi.builder;

import com.asierso.llamaapi.handlers.LlamaRequestBase;
import com.asierso.llamaapi.models.LlamaRequest;

public class LlamaRequestBaseBuilder {
    private final LlamaRequestBase req;

    public LlamaRequestBaseBuilder() {
        req = new LlamaRequest();
        req.setStream(true);
    }

    public LlamaRequestBaseBuilder useModel(String model){
        req.setModel(model);
        return this;
    }

    public LlamaRequestBaseBuilder withStream(boolean stream){
        req.setStream(stream);
        return this;
    }

    public LlamaRequestBase build(){
        return req;
    }
}
