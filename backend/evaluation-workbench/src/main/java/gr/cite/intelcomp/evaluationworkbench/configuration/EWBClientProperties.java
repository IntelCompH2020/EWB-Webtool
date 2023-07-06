package gr.cite.intelcomp.evaluationworkbench.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "ewb.client")
public class EWBClientProperties {
    private final String hostUrl;
    private final Integer maxMemory;

    @ConstructorBinding
    public EWBClientProperties(String hostUrl, Integer maxMemory) {
        this.hostUrl = hostUrl;
        this.maxMemory = maxMemory;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public Integer getMaxMemory() {
        return maxMemory;
    }
}
