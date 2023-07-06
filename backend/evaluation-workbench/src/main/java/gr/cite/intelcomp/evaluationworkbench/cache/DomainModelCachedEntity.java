package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.data.DomainModelEntity;

public class DomainModelCachedEntity extends ModelCachedEntity<DomainModelEntity>{
    @Override
    public String getCode() {
        return CommandType.MODEL_GET_DOMAIN.name();
    }
}
