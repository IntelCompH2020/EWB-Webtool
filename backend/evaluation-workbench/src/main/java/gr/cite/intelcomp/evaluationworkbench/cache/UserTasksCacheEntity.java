package gr.cite.intelcomp.evaluationworkbench.cache;

import gr.cite.intelcomp.evaluationworkbench.model.taskqueue.RunningTaskQueueItem;

public class UserTasksCacheEntity extends CachedEntity<RunningTaskQueueItem>{
    public static final String CODE = "RUNNING_TASKS_QUEUE";
    @Override
    public String getCode() {
        return CODE;
    }
}
