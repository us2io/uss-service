package io.us2.svc.seed.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParentActorTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("test-system");
    }

    @AfterClass
    public static void teardown() {
        system = null;
    }

    @Test
    public void GetHealth_WhenHealthy_HealthTrue() {
        new JavaTestKit(system) {{
            final TestProbe probe = new TestProbe(system);

            ActorRef email = system.actorOf(Props.create(ParentActor.class,
                    () -> new ParentActor(() -> new Wrapper(probe.ref()))));

            email.tell(new ActorProtocol.GetHealth(), getRef());

            // await the correct response
            ActorProtocol.Health health = expectMsgClass(duration("30 second"), ActorProtocol.Health.class);
            assertThat(health).hasFieldOrPropertyWithValue("healthy", true);

            probe.expectNoMsg();
        }};

    }

    class Wrapper extends AbstractActor {
        public Wrapper(ActorRef probe) {
            receive(ReceiveBuilder.
                    matchAny(s -> probe.forward(s, context())).
                    build());
        }
    }
}
