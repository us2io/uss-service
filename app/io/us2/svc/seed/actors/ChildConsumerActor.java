package io.us2.svc.seed.actors;

import akka.actor.Actor;
import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.us2.svc.seed.helpers.CamelHelper;
import play.Configuration;

import javax.inject.Inject;

public class ChildConsumerActor extends UntypedConsumerActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public interface Factory {
        Actor create();
    }

    @Inject
    public ChildConsumerActor(CamelHelper camel, Configuration config) {
        log.debug("Starting consumer");
    }

    @Override
    public String getEndpointUri() {
        return "foo://exampleQueue";
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof CamelMessage) {
            forward(message);
        }
    }
    private void forward(Object msg) {
        context().parent().forward(msg, context());
    }
}
