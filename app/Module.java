import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.prometheus.client.CollectorRegistry;
import io.us2.svc.seed.actors.ChildConsumerActor;
import io.us2.svc.seed.actors.ParentActor;
import io.us2.svc.seed.helpers.LifecycleHelper;
import play.libs.akka.AkkaGuiceSupport;

import javax.inject.Singleton;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

    @Override
    public void configure() {
        bind(LifecycleHelper.class).asEagerSingleton();

        bind(CollectorRegistry.class).toInstance(CollectorRegistry.defaultRegistry);

        bindActor(ParentActor.class, "svc-actor");

        bindActorFactory(ChildConsumerActor.class,
                ChildConsumerActor.Factory.class);
    }

    private Region getAwsRegion(String region) {
        return Region.getRegion(Regions.fromName(region));
    }

    @Provides
    @Singleton
    AWSCredentialsProvider provideAwsCredentials() {
        return new AWSCredentialsProviderChain(
                new InstanceProfileCredentialsProvider(),
                new EnvironmentVariableCredentialsProvider()
        );
    }

}
