package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.data.WordListEntity;

import static gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType.WORDLIST_GET;

public class WordlistCachedEntity extends CachedEntity<WordListEntity>{
    @Override
    public String getCode() {
        return WORDLIST_GET.name();
    }
}
