package fr.univnantes.termsuite.istex.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import fr.univnantes.termsuite.model.Lang;

public class LanguageDeserializer extends JsonDeserializer<Lang> {

	@Override
	public Lang deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    ObjectCodec oc = jp.getCodec();
	    
	    JsonNode node = oc.readTree(jp);

	    if(node.size() > 0) {
			String next = node.get(0).asText();
			return toTermSuiteLang(next);
		} else
	    	return null;
	}
	
	private static Lang toTermSuiteLang(String istexLang) {
		switch (istexLang) {
		case "eng":
			return Lang.EN;
		case "fra":
			return Lang.FR;
		default:
			return Lang.UNKOWN;
		}
	}
}
