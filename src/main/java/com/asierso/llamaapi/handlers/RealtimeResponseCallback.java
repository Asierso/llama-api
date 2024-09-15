package com.asierso.llamaapi.handlers;

import com.asierso.llamaapi.models.LlamaResponse;

public interface RealtimeResponseCallback {
    void run(LlamaResponse lineResponse);
}
