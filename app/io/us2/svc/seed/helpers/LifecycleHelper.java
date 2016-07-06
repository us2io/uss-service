package io.us2.svc.seed.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.hotspot.DefaultExports;
import play.inject.ApplicationLifecycle;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Singleton
public class LifecycleHelper {

    @Inject
    protected LifecycleHelper(ApplicationLifecycle lifecycle) {

        onStart();

        lifecycle.addStopHook(this::onStop);
    }

    private void onStart() {
        ObjectMapper mapper = Json.newDefaultMapper()
                // enable features and customize the object mapper here ...
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Json.setObjectMapper(mapper);

        DefaultExports.initialize();
    }

    private CompletionStage<?> onStop() {
        return null;
    }
}
