package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.data.LogicalCorpusEntity;

public class LogicalCorpusCachedEntity extends CorpusCachedEntity<LogicalCorpusEntity>{
    @Override
    public String getCode() {
        return CommandType.CORPUS_GET_LOGICAL.name();
    }
}
