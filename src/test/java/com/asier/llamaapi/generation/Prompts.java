package com.asier.llamaapi.generation;
import com.asierso.llamaapi.LlamaConnection;
import com.asierso.llamaapi.builder.LlamaPromptsBuilder;
import com.asierso.llamaapi.builder.LlamaRequestBaseBuilder;
import com.asierso.llamaapi.handlers.LlamaConnectionException;
import org.junit.Assert;
import org.junit.Test;

public class Prompts {
    private LlamaConnection llc = new LlamaConnection("http://localhost:11434");
    @Test
    public void runSingleQuestion(){

        String response = null;
        try {
            response = llc.fetch(new LlamaPromptsBuilder(new LlamaRequestBaseBuilder().useModel("llama3").withStream(false).build())
                    .appendPrompt("Hi my friend")
                    .appendPrompt("¿Whats the capital of Spain?").build()).getResponse();
            System.out.println(response);
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertNotEquals(null,response);
    }

    @Test
    public void runSingleRealtimeQuestion(){

        StringBuilder response = new StringBuilder();
        try {
            llc.fetchRealtime(new LlamaPromptsBuilder(new LlamaRequestBaseBuilder().useModel("llama3").build())
                    .appendPrompt("Hi my friend")
                    .appendPrompt("¿Whats the capital of Spain?").build(),(e)->{
                response.append(e.getResponse());
                System.out.print(e.getResponse());
            });
            System.out.println();
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertNotEquals("",response.toString());
    }
}
