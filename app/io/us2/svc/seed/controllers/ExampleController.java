package io.us2.svc.seed.controllers;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public class ExampleController extends Controller {
    /**
     * Example action.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result test() {
        return ok();
    }
}
