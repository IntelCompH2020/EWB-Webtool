package gr.cite.intelcomp.evaluationworkbench.model.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import gr.cite.intelcomp.evaluationworkbench.model.EWBTopicBeta;

import java.io.IOException;
import java.util.List;

public class EWBBetaDeserializer extends JsonDeserializer<List<EWBTopicBeta>> {
    @Override
    public List<EWBTopicBeta> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return EWBTopicBeta.mapToList(p.getValueAsString());
    }
}
