package com.asierso.llamaapi;

import com.asierso.llamaapi.handlers.LlamaConnectionException;
import com.asierso.llamaapi.models.LlamaMessage;
import com.asierso.llamaapi.models.LlamaRequest;
import com.asierso.llamaapi.models.LlamaResponse;
import com.asierso.llamaapi.handlers.RealtimeResponseCallback;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LlamaConnection {
    private final String url;

    public LlamaConnection(String url) {
        this.url = url;
    }

    private URL buildUri(String service) throws IOException{
        return new URL(url + service);
    }

    public LlamaResponse fetch(LlamaRequest req) throws LlamaConnectionException {
        return fetchRealtime(req,null);
    }

    public LlamaResponse fetchRealtime(LlamaRequest req, RealtimeResponseCallback rtCallback) throws LlamaConnectionException {
        if(req.getModel().isBlank())
            throw new LlamaConnectionException(404,"Model not found");

        //Llama response objects
        LlamaResponse llres = null;
        StringBuilder response = new StringBuilder();
        StringBuilder message = new StringBuilder();
        String messageRole = null;

        try {
            //Open http connection with llama server
            HttpURLConnection con = getHttpURLConnection(req);

            //If success, process recieved data
            if (con.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                //Read line per line
                while ((responseLine = in.readLine()) != null) {

                    //Create json format from line response and execute realtime response if proceed
                    llres = new Gson().fromJson(responseLine, LlamaResponse.class);
                    if (rtCallback != null)
                        rtCallback.run(llres);

                    //Create fully sentence for messages and responses (the active service)
                    if(llres.getResponse() != null)
                        response.append(llres.getResponse());

                    if(llres.getMessage() != null) {
                        message.append(llres.getMessage().getContent());
                        messageRole = llres.getMessage().getRole();
                    }
                }
                in.close();
            } else {
                //Throw error (some error at the connection with llama
                throw new LlamaConnectionException(con.getResponseCode(), con.getResponseMessage());
            }
        }catch(IOException e){
            throw new LlamaConnectionException(0,e.getMessage());
        }

        //Create fully response with some headers data
        LlamaResponse fullyResponse = null;

        if(llres != null)
            fullyResponse = new LlamaResponse(llres.getModel(),response.toString(),llres.isDone(),messageRole == null? null : new LlamaMessage(messageRole,message.toString()));
        return fullyResponse;
    }

    private HttpURLConnection getHttpURLConnection(LlamaRequest req) throws IOException {
        String GENERATION_SERVICE = "/api/generate";
        String CHAT_SERVICE = "/api/chat";

        //Choose llama service to use
        HttpURLConnection con = (HttpURLConnection) (req.getMessages() != null?
                buildUri(CHAT_SERVICE).openConnection() :
                buildUri(GENERATION_SERVICE).openConnection());

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        //Prepare input
        //String jsonInput = "{\"model\":\"" + req.getModel() + "\",\"prompt\":\"" + req.getPrompt() + "\"}";
        String jsonInput = new Gson().toJson(req);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return con;
    }
}
