package gr.cite.intelcomp.evaluationworkbench.model;

public class EWBTopDoc {
    private String id;
    private String title;
    private Double topic;
    private Integer words;

    public EWBTopDoc(String id) {
        this.id = id;
    }

    public EWBTopDoc(String id, Double topic, Integer words) {
        this.id = id;
        this.topic = topic;
        this.words = words;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getTopic() {
        return topic;
    }

    public void setTopic(Double topic) {
        this.topic = topic;
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }
}
