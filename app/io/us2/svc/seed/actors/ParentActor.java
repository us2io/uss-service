package io.us2.svc.seed.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import play.libs.akka.InjectedActorSupport;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;

import static io.us2.svc.seed.actors.ActorProtocol.*;

@SuppressWarnings("PMD.SingularField")
public class ParentActor extends AbstractActor implements InjectedActorSupport {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final ExecutionContextExecutor ec = context().dispatcher();

    private final ActorRef exampleChild;

    @Inject
    public ParentActor(ChildConsumerActor.Factory exampleChild) {
        this.exampleChild = injectedChild(exampleChild::create, "example-child");

        receive(ReceiveBuilder.
                match(GetHealth.class, s -> {
                    sender().tell(new Health(true), self());
                }).
                matchAny(o -> log.warning("received unknown message: {}", o)).build()
        );
    }
}
