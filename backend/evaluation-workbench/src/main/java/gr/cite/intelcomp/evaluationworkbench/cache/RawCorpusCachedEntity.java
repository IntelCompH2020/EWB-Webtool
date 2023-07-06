package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.data.RawCorpusEntity;

public class RawCorpusCachedEntity extends CorpusCachedEntity<RawCorpusEntity>{
    @Override
    public String getCode() {
        return CommandType.CORPUS_GET_RAW.name();
    }
}
