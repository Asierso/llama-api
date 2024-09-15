package com.asierso.llamaapi.handlers;

public interface LlamaRequestBase {
    String getModel();
    void setModel(String model);
    boolean isStream();
    void setStream(boolean stream);
}
