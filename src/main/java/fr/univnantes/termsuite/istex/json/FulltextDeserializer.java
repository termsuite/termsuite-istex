package fr.univnantes.termsuite.istex.json;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class FulltextDeserializer extends JsonDeserializer<Map<RawType, URL>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FulltextDeserializer.class);
	
	@Override
	public Map<RawType, URL> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Map<RawType, URL> map = new HashMap<>();
		ObjectCodec oc = jp.getCodec();
	    JsonNode node = oc.readTree(jp);
	    for(int i = 0 ; i < node.size() ; i ++) {
			String extension = node.get(i).get("extension").asText();
			String uri = node.get(i).get("uri").asText();
			RawType rawType = RawType.forName(extension);
			if(rawType == null)
				LOGGER.warn("Unsupported raw type: {}", extension);
			else
				map.put(rawType, new URL(uri));
		} 
	    return map;
	}
	
}
