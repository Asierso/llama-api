import com.asierso.llamaapi.LlamaConnection;
import com.asierso.llamaapi.builder.LlamaDialogsBuilder;
import com.asierso.llamaapi.builder.LlamaPromptsBuilder;
import com.asierso.llamaapi.builder.LlamaRequestBaseBuilder;
import com.asierso.llamaapi.handlers.LlamaConnectionException;
import com.asierso.llamaapi.models.LlamaMessage;
import org.junit.Assert;
import org.junit.Test;

public class Dialogues {
    private LlamaConnection llc = new LlamaConnection("http://localhost:11434");
    @Test
    public void runSimpleDialog(){
        LlamaMessage response = null;
        try {
            response = llc.fetch(new LlamaDialogsBuilder(new LlamaRequestBaseBuilder().useModel("llama3").withStream(false).build())
                    .createDialog(LlamaMessage.ASSISTANT_ROLE,"Hi my friend, how're you?")
                    .createDialog(LlamaMessage.USER_ROLE,"Im fine and you?")
                    .createDialog(LlamaMessage.ASSISTANT_ROLE,"Im fine too. Lets go for a walk, shall we?")
                    .createDialog(LlamaMessage.USER_ROLE,"Yes. I want to walk with you, can I?").build()).getMessage();
            System.out.println(response.getRole() + ": " + response.getContent());
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertEquals("assistant",response.getRole());
    }

    @Test
    public void runSingleRealtimeDialog(){
        StringBuilder response = new StringBuilder();
        try {
            llc.fetchRealtime(new LlamaDialogsBuilder(new LlamaRequestBaseBuilder().useModel("llama3").build())
                    .createDialog(LlamaMessage.ASSISTANT_ROLE,"Hi my friend, how're you?")
                    .createDialog(LlamaMessage.USER_ROLE,"Im fine and you?")
                    .createDialog(LlamaMessage.ASSISTANT_ROLE,"Im fine too. Lets go for a walk, shall we?")
                    .createDialog(LlamaMessage.USER_ROLE,"Yes. I want to walk with you, can I?").build(),(e)->{
                response.append(e.getMessage().getContent());
                System.out.print(e.getMessage().getContent());
            });

            Assert.assertNotEquals("",response.toString());
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertNotEquals("",response.toString());
    }
}
