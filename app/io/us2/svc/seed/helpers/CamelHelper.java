package io.us2.svc.seed.helpers;

import akka.actor.ActorSystem;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import org.apache.camel.impl.SimpleRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CamelHelper {
    private final SimpleRegistry registry;

    @Inject
    public CamelHelper(ActorSystem system) {
        Camel camel = CamelExtension.get(system);
        registry = new SimpleRegistry();

        camel.context().setRegistry(registry);
    }

    public void add(String key, Object value) {
        registry.put(key, value);
    }
}
