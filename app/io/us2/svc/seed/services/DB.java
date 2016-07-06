package io.us2.svc.seed.services;

import akka.actor.ActorSystem;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import play.db.Database;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class DB {
    private final ExecutionContextExecutor databaseContext;
    private Database db;

    @Inject
    public DB(Database db, ActorSystem actorSystem) {
        this.db = db;
        this.databaseContext = actorSystem.dispatchers().lookup("contexts.database");
    }

    public <A> CompletionStage<A> query(Function<DSLContext, A> block) {
        return CompletableFuture.supplyAsync(() -> {
            return db.withConnection(con -> {
                return block.apply(DSL.using(con));
            });
        }, databaseContext);
    }

    public <A> CompletionStage<A> withTransaction(Function<DSLContext, A> block) {
        return CompletableFuture.supplyAsync(() -> {
            return db.withTransaction(con -> {
                return block.apply(DSL.using(con));
            });
        }, databaseContext);
    }

    public CompletionStage<Boolean> isValid(int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            return db.withConnection(con -> {
                return con.isValid(timeout);
            });
        }, databaseContext);
    }
}
