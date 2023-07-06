package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.common.enums.ModelType;
import gr.cite.intelcomp.evaluationworkbench.common.enums.Visibility;
import gr.cite.tools.data.query.Lookup;

import java.util.List;
import java.util.UUID;

public class ModelLookup extends Lookup {

    private String like;
    private List<Visibility> visibilities;
    private List<UUID> ids;

    private String creator;

    private Boolean mine;

    private ModelType modelType;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public List<Visibility> getVisibilities() {
        return visibilities;
    }

    public void setVisibilities(List<Visibility> visibilities) {
        this.visibilities = visibilities;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Boolean getMine() {
        return mine;
    }

    public void setMine(Boolean mine) {
        this.mine = mine;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }
}
