package fr.univnantes.termsuite.istex.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class SubjectDeserializer extends JsonDeserializer<List<String>> {

	@Override
	public List<String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    ObjectCodec oc = jp.getCodec();
	    
	    JsonNode node = oc.readTree(jp);

	    List<String> subjects = new ArrayList<>();
	    for(int i = 0 ; i < node.size() ; i ++) {
			String next = node.get(i).get("value").asText();
			subjects.add(next);
		} 
	    
	    return subjects;
	}
	
}
