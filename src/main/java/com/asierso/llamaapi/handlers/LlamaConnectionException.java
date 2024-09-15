package com.asierso.llamaapi.handlers;

public class LlamaConnectionException extends Exception{
    public LlamaConnectionException(int status,String text){
        super(status + ": " + text);
    }
}
