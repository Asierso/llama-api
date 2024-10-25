package com.asierso.llamaapi;

import com.asierso.llamaapi.handlers.LlamaConnectionException;
import com.asierso.llamaapi.models.AIModel;
import com.asierso.llamaapi.models.LlamaMessage;
import com.asierso.llamaapi.models.LlamaRequest;
import com.asierso.llamaapi.models.LlamaResponse;
import com.asierso.llamaapi.models.ModelList;
import com.asierso.llamaapi.handlers.RealtimeResponseCallback;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LlamaConnection {
    private final String url;
    private enum ServiceType { 
    	
    	GET {
    		@Override
    		public String toString() {
    			return "GET";
    		}
    	},
    	
    	POST {
    		@Override
    		public String toString() {
    			return "GET";
    		}
    	}
    }

    /**
     * Creates a new connection with Llama API
     * @param url URL of llama API using this format (protocol://direction:port)
     */
    public LlamaConnection(String url) {
        this.url = url;
    }

    @SuppressWarnings("deprecation")
    private URL buildUri(String service) throws IOException{
        return new URL(url + service);
    }

    /**
     * Make a fetch to llama API with the specified request info.
     * The provided LlamaRequest must have a valid model name.
     * You can build it using "LlamaPromptsBuilder" or "LlamaDialogsBuilder"
     *
     * @see com.asierso.llamaapi.builder.LlamaPromptsBuilder
     * @see com.asierso.llamaapi.builder.LlamaDialogsBuilder
     * @param req Request data to send
     * @return Llama API response
     * @throws LlamaConnectionException Errors at connection or malformed Llama request
     */
    public LlamaResponse fetch(LlamaRequest req) throws LlamaConnectionException {
        return fetchRealtime(req,null);
    }

    /**
     * Make a fetch to llama API with the specified request info and
     * execute callback every time that API response is recieved (stream).
     * The provided LlamaRequest must have a valid model name.
     * You can build it using "LlamaPromptsBuilder" or "LlamaDialogsBuilder"
     *
     * @see com.asierso.llamaapi.builder.LlamaPromptsBuilder
     * @see com.asierso.llamaapi.builder.LlamaDialogsBuilder
     * @param req Request data to send
     * @param rtCallback Realtime callback method that will be executed
     * @return Llama API response
     * @throws LlamaConnectionException Errors at connection or malformed Llama request
     */
    public LlamaResponse fetchRealtime(LlamaRequest req, RealtimeResponseCallback rtCallback) throws LlamaConnectionException {
        if(req.getModel().isBlank())
            throw new LlamaConnectionException(404,"Model not found");

        //Llama response objects
        LlamaResponse llamaResponse = null;
        StringBuilder response = new StringBuilder();
        StringBuilder message = new StringBuilder();
        String messageRole = null;

        try {
            //Open http connection with llama server
            HttpURLConnection con = startServiceWith(req);

            //If success, process data
            if (con.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                //Read line per line
                while ((responseLine = in.readLine()) != null) {

                    //Create json format from line response and execute realtime response if proceed
                    llamaResponse = new Gson().fromJson(responseLine, LlamaResponse.class);
                    if (rtCallback != null)
                        rtCallback.run(llamaResponse);

                    //Create fully sentence for messages and responses (the active service)
                    if(llamaResponse.getResponse() != null)
                        response.append(llamaResponse.getResponse());

                    if(llamaResponse.getMessage() != null) {
                        message.append(llamaResponse.getMessage().getContent());
                        messageRole = llamaResponse.getMessage().getRole();
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

        if(llamaResponse != null)
            fullyResponse = new LlamaResponse(llamaResponse.getModel(),response.toString(),llamaResponse.isDone(),messageRole == null? null : new LlamaMessage(messageRole,message.toString()));
        return fullyResponse;
    }
    
    public List<AIModel> getModels() throws LlamaConnectionException {
    	ModelList models = null;
    	try {
    		//Fetch models api
    		HttpURLConnection con = getHttpURLConnection("/api/tags",ServiceType.GET);
    		//If success, process data
            if (con.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                //Read line per line
                while ((responseLine = in.readLine()) != null) {
                    //Create json format from models
                    models = new Gson().fromJson(responseLine, ModelList.class);
                    
                }
                in.close();
            } else {
                //Throw error (some error at the connection with llama)
                throw new LlamaConnectionException(con.getResponseCode(), con.getResponseMessage());
            }
    	}catch (IOException e) {
    		throw new LlamaConnectionException(0,e.getMessage());
    	}
    	
    	if(models == null)
    		return null;
    	
    	return models.getModels();
    }
    
    private HttpURLConnection getHttpURLConnection(String service,ServiceType stype) throws IOException {
        //Get llama service
        HttpURLConnection con = (HttpURLConnection) buildUri(service).openConnection();
        
        
        //Adjust connection settings to use REST
        con.setRequestMethod(stype.toString());
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        
        return con;
    }

    private HttpURLConnection startServiceWith(LlamaRequest req) throws IOException {
        String GENERATION_SERVICE = "/api/generate";
        String CHAT_SERVICE = "/api/chat";

        //Choose llama service to use
        HttpURLConnection con =  getHttpURLConnection(req.getMessages() != null?
                CHAT_SERVICE :
                GENERATION_SERVICE,ServiceType.POST);
        
        //Prepare json request to send
        String jsonInput = new Gson().toJson(req);

        //Write json request on stream
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        return con;
    }
}
