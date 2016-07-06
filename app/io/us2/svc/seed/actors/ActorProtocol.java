package io.us2.svc.seed.actors;

import lombok.Value;

public class ActorProtocol {
    static public class GetHealth {}

    @Value
    static public class Health {
        boolean healthy;
    }

    public static class Kill {}
}
