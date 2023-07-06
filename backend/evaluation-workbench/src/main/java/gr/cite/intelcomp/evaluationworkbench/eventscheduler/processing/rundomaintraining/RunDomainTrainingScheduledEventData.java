package gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.rundomaintraining;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.TrainingScheduledEventData;
import gr.cite.intelcomp.evaluationworkbench.model.persist.domainclassification.DomainClassificationRequestPersist;

import java.util.UUID;

public class RunDomainTrainingScheduledEventData extends TrainingScheduledEventData {

    private final DomainClassificationRequestPersist request;

    @JsonCreator
    public RunDomainTrainingScheduledEventData(
            @JsonProperty("trainingTaskRequestId") UUID trainingTaskRequestId,
            @JsonProperty("request") DomainClassificationRequestPersist request) {
        super(trainingTaskRequestId);
        this.request = request;
    }

    public DomainClassificationRequestPersist getRequest() {
        return request;
    }
}
