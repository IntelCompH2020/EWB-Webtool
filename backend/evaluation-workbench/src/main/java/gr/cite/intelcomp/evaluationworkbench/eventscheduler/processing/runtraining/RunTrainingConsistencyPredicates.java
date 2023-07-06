package gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.runtraining;

import gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.ConsistencyPredicates;

import java.util.UUID;

public class RunTrainingConsistencyPredicates implements ConsistencyPredicates {

    private final UUID trainingTaskRequestId;

    public RunTrainingConsistencyPredicates(UUID trainingTaskRequestId) {
        this.trainingTaskRequestId = trainingTaskRequestId;
    }

    public UUID getTrainingTaskRequestId() {
        return trainingTaskRequestId;
    }
}
