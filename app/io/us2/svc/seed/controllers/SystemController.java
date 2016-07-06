package io.us2.svc.seed.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.PatternsCS;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.common.TextFormat;
import io.us2.svc.seed.actors.ActorProtocol;
import io.us2.svc.seed.services.DB;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains system level endpoints.
 */
public class SystemController extends Controller {
    static final Counter healthChecks = Counter.build()
            .name("health_checks")
            .help("Health checks.").register();

    private final ActorRef parentActor;
    private DB db;
    private CollectorRegistry registry;
    private final LoggingAdapter log;


    @Inject
    public SystemController(@Named("svc-actor") ActorRef parentActor,
                            DB db, ActorSystem system, CollectorRegistry registry) {
        this.parentActor = parentActor;
        this.db = db;
        this.registry = registry;
        log = Logging.getLogger(system, this);
    }

    /**
     * Conduct health checks and return status
     */
    public CompletionStage<Result> health() {
        // TODO: Consider authenticating endpoint

        healthChecks.inc();

        CompletionStage<Boolean> emailHealth = PatternsCS.ask(parentActor, new ActorProtocol.GetHealth(), 1000)
                .thenApply(ActorProtocol.Health.class::cast)
                .thenApply(ActorProtocol.Health::isHealthy);

        CompletionStage<Boolean> dbHealth = db.isValid(1000);

        return emailHealth
                .thenCombine(dbHealth, Boolean::logicalAnd)
                .thenApply(result -> result ? ok() : internalServerError());
    }

    /**
     * Prepare and deliver Prometheus metrics.
     */
    public Result metrics() {
        // TODO: Consider authenticating endpoint

        StringWriter writer = new StringWriter();
        try {
            TextFormat.write004(writer ,registry.metricFamilySamples());
            return ok(writer.toString()).as(TextFormat.CONTENT_TYPE_004);
        } catch (IOException e) {
            log.error(e, "Unable to write metrics");
            return internalServerError();
        }
    }

    /**
     * Initiate graceful shutdown.
     */
    public Result kill() {
        // TODO: Consider authenticating endpoint

        parentActor.tell(new ActorProtocol.Kill(), null);
        return ok();
    }

}
