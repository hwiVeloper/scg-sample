package dev.hwiveloper.sample.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TheSsenFilter extends AbstractGatewayFilterFactory<TheSsenFilter.Config> {
    public TheSsenFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            HttpMethod requestMethod = request.getMethod();
            String port = request.getURI().getPort() != -1 ? String.valueOf(request.getURI().getPort()) : "";
            String requestEndpoint = request.getURI().getScheme() + "://" + request.getURI().getHost() + ":" + port + request.getURI().getPath();

            StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            log.info("▷START");
            log.info("TheSsenFilter Pre filter: method -> {}, request id -> {}, endpoint -> {}", requestMethod, request.getId(), requestEndpoint);
            log.info("TheSsenFilter Pre filter: endpoint -> {}", requestEndpoint);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("TheSsenFilter Post filter: response code -> {}", response.getStatusCode());
                stopWatch.stop();
                log.info("▷END [{}ms]", stopWatch.getTotalTimeMillis());
            }));
        };
    }

    public static class Config {

    }
}
