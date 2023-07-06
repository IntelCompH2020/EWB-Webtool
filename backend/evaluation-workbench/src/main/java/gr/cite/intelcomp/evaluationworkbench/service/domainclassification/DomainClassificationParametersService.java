package gr.cite.intelcomp.evaluationworkbench.service.domainclassification;

import gr.cite.intelcomp.evaluationworkbench.model.persist.domainclassification.DomainClassificationRequestPersist;

import java.nio.file.Path;
import java.util.UUID;

import static gr.cite.intelcomp.evaluationworkbench.service.domainclassification.DomainClassificationParametersServiceJson.*;

public abstract class DomainClassificationParametersService {

    public abstract Path generateConfigurationFile(DomainClassificationRequestPersist config, UUID userId);

    public abstract void updateConfigurationFile(String name, String description, String visibility);

    public abstract DomainClassificationParametersModel getConfigurationModel(String name);

}
