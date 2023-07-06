package gr.cite.intelcomp.evaluationworkbench.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(EWBClientProperties.class)
public class EWBClientConfiguration {

    private final EWBClientProperties properties;

    public EWBClientConfiguration(EWBClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Qualifier("ewbClient")
    public WebClient ewbClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(
                clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(properties.getMaxMemory())
        ).build();
        return WebClient.builder().baseUrl(properties.getHostUrl())
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }
}
