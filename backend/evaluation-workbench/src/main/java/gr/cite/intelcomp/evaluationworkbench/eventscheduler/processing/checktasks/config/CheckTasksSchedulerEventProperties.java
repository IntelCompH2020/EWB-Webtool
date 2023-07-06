package gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.checktasks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


@ConfigurationProperties(prefix = "event-scheduler.events.check-tasks")
public class CheckTasksSchedulerEventProperties {

    private final Long checkIntervalInSeconds;

    @ConstructorBinding
    public CheckTasksSchedulerEventProperties(Long checkIntervalInSeconds) {
        this.checkIntervalInSeconds = checkIntervalInSeconds;
    }

    public Long getCheckIntervalInSeconds() {
        return checkIntervalInSeconds;
    }

}
