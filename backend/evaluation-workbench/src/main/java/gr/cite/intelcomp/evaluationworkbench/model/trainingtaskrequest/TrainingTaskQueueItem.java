package gr.cite.intelcomp.evaluationworkbench.model.trainingtaskrequest;

import gr.cite.intelcomp.evaluationworkbench.model.taskqueue.RunningTaskQueueItem;
import gr.cite.intelcomp.evaluationworkbench.model.taskqueue.RunningTaskType;

public class TrainingTaskQueueItem extends RunningTaskQueueItem {

    public TrainingTaskQueueItem() {
        super(RunningTaskType.training);
    }

}
