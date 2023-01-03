package com.example.starter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
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
  private static int serverPort = Integer.parseInt(System.getProperty("serverPort", "8088"));
  private static String otlpEndpointUrl = System.getProperty("endpointUrl", "http://127.0.0.1:4317");

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions()
      .setTracingOptions(new OpenTelemetryOptions(getOpenTelemetry()))
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
      .rxListen(serverPort).ignoreElement()
      .subscribe(
        () -> logger.info("Vertx application started at port: " + serverPort),
        error -> logger.error("Failed to start Vertx application.", error)
      );
  }

  private static void server(RoutingContext rc) {
    rc.response().headers().add("X-MY-HEADER", "my-custom-header");
    rc.response().end("SERVER RESPONSE");
  }

  private static void test(RoutingContext rc) {
    // when this endpoint triggered for 2nd time onward, will cause the error:
    //   io.netty.handler.codec.http2.Http2Exception: invalid header name [X-B3-TraceId]
    webClient.get(serverPort, "127.0.0.1", "/server")
      .rxSend()
      .doOnSuccess(bufferHttpResponse -> {
        logger.info("Headers:");
        bufferHttpResponse.headers().forEach(entry -> logger.info(entry.getKey() + " -> " + entry.getValue()));

        rc.response().end(new String(bufferHttpResponse.bodyAsBuffer().getBytes()));
      })
      .doOnError(throwable -> {
        rc.response().end("Error");
      })
      .ignoreElement()
      .subscribe(() -> {
      }, error -> logger.error("error:", error));
  }

  private static OpenTelemetry getOpenTelemetry() {
    logger.info("OTLP Endpoint: " + otlpEndpointUrl);
    return OpenTelemetrySdk.builder()
      .setTracerProvider(
        SdkTracerProvider.builder()
          .addSpanProcessor(BatchSpanProcessor.builder(
              OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpointUrl)
                .build())
            .build())
          .setResource(Resource.getDefault().merge(Resource.create(Attributes.of(
            AttributeKey.stringKey("service.name"), "my-service"
          ))))
          .build())
      .setPropagators(ContextPropagators.create(B3Propagator.injectingMultiHeaders()))
      .buildAndRegisterGlobal();
  }
}
