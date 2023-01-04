package com.example.starter;

import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.reactivex.disposables.Disposable;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private static WebClient webClient;
  private static final int SERVER_PORT = Integer.parseInt(System.getProperty("serverPort", "8080"));

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions()
      .setTracingOptions(new OpenTelemetryOptions(OpenTelemetrySdk.builder()
        .setPropagators(ContextPropagators.create(B3Propagator.injectingMultiHeaders()))
        .buildAndRegisterGlobal()))
    );

    webClient = WebClient.create(vertx, new WebClientOptions()
      .setProtocolVersion(HttpVersion.HTTP_2)
      .setTracingPolicy(TracingPolicy.ALWAYS)
    );

    Router router = Router.router(vertx);
    router.get("/server").handler(Main::server);
    router.get("/test").handler(Main::test);

    vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(SERVER_PORT).ignoreElement()
      .subscribe(
        () -> logger.info("Vertx application started at port: " + SERVER_PORT),
        error -> logger.error("Failed to start Vertx application.", error)
      );
  }

  private static void server(RoutingContext rc) {
    logger.info("Accepting /server request...");
    rc.response().headers().add("X-MY-HEADER", "my-custom-header");
    rc.response().end("SERVER OK");
    logger.info("/server responded.");
  }

  private static Disposable test(RoutingContext rc) {
    logger.info("Handling /test request...");

    // when this endpoint triggered for 2nd time onward, will cause the following error:
    //   io.netty.handler.codec.http2.Http2Exception: invalid header name [X-B3-TraceId]
    return webClient.get(SERVER_PORT, "127.0.0.1", "/server")
      .rxSend()
      .doOnSuccess(bufferHttpResponse -> {
        logger.info("Headers:");
        bufferHttpResponse.headers().forEach(entry -> logger.info(entry.getKey() + " -> " + entry.getValue()));
        rc.response().end(new String(bufferHttpResponse.bodyAsBuffer().getBytes()));
      })
      .doOnError(throwable -> {
        rc.response().end("Client Error");
      })
      .ignoreElement()
      .subscribe(
        () -> {
        },
        throwable -> logger.info("Error while client request", throwable)
      );
  }

}
