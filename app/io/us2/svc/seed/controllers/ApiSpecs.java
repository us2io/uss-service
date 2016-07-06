package io.us2.svc.seed.controllers;

import akka.actor.ActorSystem;
import com.iheart.playSwagger.SwaggerSpecGenerator;
import play.api.libs.json.JsValue;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ApiSpecs extends Controller {
    private final SwaggerSpecGenerator generator;

    @Inject
    public ApiSpecs(ActorSystem system) {
        generator = new SwaggerSpecGenerator(null, "application/json", this.getClass().getClassLoader());
    }

    public CompletionStage<Result> specs() {
        return CompletableFuture
                .supplyAsync(this::getSpecs)
                .thenApply(Results::ok);
    }

    public Result redirect() {
        return redirect("swagger-ui/index.html?url=/docs/swagger.json");
    }

    private String getSpecs() {
        JsValue ret = generator.generate(generator.defaultRoutesFile()).get();

        return play.api.libs.json.Json.prettyPrint(ret);
    }
}
