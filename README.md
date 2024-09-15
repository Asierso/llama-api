# Llama API - Java
API for Java to interact with Ollama IA.

---
## 🐳 Deploy Ollama using Docker
Use these steps to run Ollama IA in Docker container
1. Run in shell `docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama`
2. To install models use `docker exec -it ollama ollama run <model-name>`
---
## 📝 Examples of API uses
### Ask a prompt
You can send a single prompt to Ollama using the following code
```java
//Declare the connection with Ollama service
LlamaConnection connection = new LlamaConnection("http://localhost:11434");

//Create the base head for the connection to Ollama
LlamaRequestBase requestBase = new LlamaRequestBaseBuilder()
        .useModel("llama3")
        .withStream(false)
        .build();

//Make the request with specified prompt
String response = connection.fetch(new LlamaPromptsBuilder(requestBase)
        .appendPrompt("Hi my friend")
        .appendPrompt("¿What's the capital of Spain?")
        .build()).getResponse();
```

### Have a conversation dialog
You can create a thread of prompts and answers that would be processed by Ollama like a conversation format using the following lines
```java
//Declare the connection with Ollama service
LlamaConnection connection = new LlamaConnection("http://localhost:11434");

//Create the base head for the connection to Ollama
LlamaRequestBase requestBase = new LlamaRequestBaseBuilder()
        .useModel("llama3")
        .withStream(false)
        .build();

//Make the request with specified dialogue and get the content of the last message
String response = connection.fetch(new LlamaDialogsBuilder(requestBase)
        .createDialog("assistant","Hey my friend. Lets go for a walk, shall we?")
        .createDialog("user","Yes. But want to hug you, can I?")
        .build()).getMessage().getContent();
```