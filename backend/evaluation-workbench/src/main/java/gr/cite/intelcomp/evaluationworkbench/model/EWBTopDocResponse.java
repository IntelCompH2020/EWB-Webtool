package gr.cite.intelcomp.evaluationworkbench.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gr.cite.intelcomp.evaluationworkbench.model.deserializer.EWBThetaDeserializer;

import java.util.List;

public class EWBTopDocResponse {

    private String id;
    @JsonDeserialize(using = EWBThetaDeserializer.class)
    private List<EWBTheta> thetas;

    @JsonProperty("num_words_per_doc")
    private Integer words;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EWBTheta> getThetas() {
        return thetas;
    }

    public void setThetas(List<EWBTheta> thetas) {
        this.thetas = thetas;
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }
}
