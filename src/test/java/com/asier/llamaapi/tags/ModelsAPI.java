package com.asier.llamaapi.tags;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.asierso.llamaapi.LlamaConnection;
import com.asierso.llamaapi.handlers.LlamaConnectionException;
import com.asierso.llamaapi.models.AIModel;
import com.asierso.llamaapi.models.ModelList;

public class ModelsAPI {
	private LlamaConnection llc = new LlamaConnection("http://localhost:11434");
	@Test
	public void getServerModels(){
		List<AIModel> models = null;
        try {
        	models = llc.getModels();
        	if(models != null) {
        		System.out.println("Models: " + Arrays.toString(models.toArray()));
        	}
        }
        catch(LlamaConnectionException e){
            Assert.fail();
        }

        Assert.assertNotEquals(models, null);
	}
}
