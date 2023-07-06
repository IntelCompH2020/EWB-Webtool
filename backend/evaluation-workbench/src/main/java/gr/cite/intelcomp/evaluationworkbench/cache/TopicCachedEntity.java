package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.data.topic.TopicEntity;

public class TopicCachedEntity extends CachedEntity<TopicEntity> {
    public static final String CODE = CommandType.TOPIC_GET.name();
    @Override
    public String getCode() {
        return CODE;
    }
}
