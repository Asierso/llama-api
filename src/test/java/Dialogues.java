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
                    .createDialog("assistant","Hi my friend, how're you?")
                    .createDialog("user","Im fine and you?")
                    .createDialog("assistant","Im fine too. Lets go for a walk, shall we?")
                    .createDialog("user","Yes. I want to hug you, can I?").build()).getMessage();
            System.out.println(response.getRole() + ": " + response.getContent());
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertEquals("assistant",response.getRole());
    }
}
