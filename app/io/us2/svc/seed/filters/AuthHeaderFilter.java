package io.us2.svc.seed.filters;

import akka.stream.Materializer;
import play.Configuration;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import play.routing.Router;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class AuthHeaderFilter extends Filter {
    private final String auth;
    private final Base64.Decoder decoder = Base64.getDecoder();

    @Inject
    public AuthHeaderFilter(Materializer mat, Configuration config) {
        super(mat);
        auth = config.getString("uss.auth.pass");
    }

    @Override
    public CompletionStage<Result> apply(
        Function<RequestHeader, CompletionStage<Result>> next,
        RequestHeader requestHeader) {

        if ("/healthz".equals(requestHeader.tags().get(Router.Tags.ROUTE_PATTERN))) {
            return next.apply(requestHeader);
        }

        if (!isAuthenticated(requestHeader.getHeader(Http.HeaderNames.AUTHORIZATION))) {
            return CompletableFuture.completedFuture(
                    Results
                            .unauthorized()
                            .withHeader(Http.HeaderNames.WWW_AUTHENTICATE, "Basic"));
        }

        return next.apply(requestHeader);
    }

    private boolean isAuthenticated(String authorization) {
        return auth == null || passwordMatch(authorization);
    }

    private boolean passwordMatch(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return false;
        }

        String[] parts = authorization.split(" ");

        if (parts.length != 2) {
            return false;
        }

        String token = parts[parts.length - 1];
        String decoded = new String(decoder.decode(token), StandardCharsets.UTF_8);

        return decoded.endsWith(auth);
    }

}
