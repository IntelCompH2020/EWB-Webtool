package gr.cite.intelcomp.evaluationworkbench.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TrainingParametersProperties.class)
public class TrainingParametersConfiguration {
}
