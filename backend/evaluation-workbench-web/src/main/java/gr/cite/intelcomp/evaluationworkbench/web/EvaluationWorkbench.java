package gr.cite.intelcomp.evaluationworkbench.web;

import gr.cite.intelcomp.evaluationworkbench.model.validation.ValidationUtils;
import gr.cite.intelcomp.evaluationworkbench.common.utils.EventSchedulerUtils;
/*import gr.cite.intelcomp.evaluationworkbench.service.containermanagement.ContainerStartupUtils;
import io.kubernetes.client.openapi.ApiException;*/
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
@ComponentScan({
        "gr.cite.intelcomp.evaluationworkbench",
        "gr.cite.tools",
        "gr.cite.commons"})
@EntityScan({
        "gr.cite.intelcomp.evaluationworkbench.data"})
public class EvaluationWorkbench {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(EvaluationWorkbench.class, args);
        EventSchedulerUtils.initializeRunningTasksCheckEvent(applicationContext);
        ValidationUtils.tapTopicModelParameterValidator();
        /*try {
            ContainerStartupUtils.initServices(applicationContext);
        } catch (IOException | ApiException e) {
            throw new RuntimeException(e);
        }*/
    }

}
