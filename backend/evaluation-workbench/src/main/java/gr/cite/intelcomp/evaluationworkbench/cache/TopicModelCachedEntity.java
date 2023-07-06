package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.data.TopicModelEntity;

public class TopicModelCachedEntity extends ModelCachedEntity<TopicModelEntity> {
    @Override
    public String getCode() {
        return CommandType.MODEL_GET_TOPIC.name();
    }
}
