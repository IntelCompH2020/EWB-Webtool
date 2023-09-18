package gr.cite.intelcomp.evaluationworkbench.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EWBSimilarityScore {

    @JsonProperty("id_1")
    private String id1;

    @JsonProperty("id_2")
    private String id2;

    private Float score;

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

}
