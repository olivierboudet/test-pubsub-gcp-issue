package com.example.demo;

import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.core.Credentials;
import org.springframework.cloud.gcp.core.DefaultCredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;

import java.io.IOException;

@Configuration
public class PubSubConfiguration {

    @Value("${pubsub.project-id}")
    private String projectId;

    @Value("${pubsub.keep-alive}")
    private int keepAlive = 5;

    @Value("${pubsub.subscription-name}")
    private String subscriptionName;

    @Value("${pubsub.key}")
    private String pubsubKey;

    @Bean
    public Subscriber pubsub(TransportChannelProvider transportChannelProvider) throws IOException {
        Credentials credentials = new Credentials();
        credentials.setEncodedKey(this.pubsubKey);

        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionName);
        Subscriber subscriber = Subscriber.newBuilder(subscription, (message, consumer) -> {
            System.err.println(message);
        })
                .setChannelProvider(transportChannelProvider)
                .setCredentialsProvider(new DefaultCredentialsProvider(() -> credentials)).build();
        subscriber.startAsync();
        return subscriber;

    }

    @Bean
    public TransportChannelProvider transportChannelProvider() {
        return InstantiatingGrpcChannelProvider.newBuilder()
                .setKeepAliveTime(Duration.ofMinutes(this.keepAlive))
                .build();
    }
}
