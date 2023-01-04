1. Start the main appliction of the Vertx application at com.example.starter.Main. 
   Once started, the Vertx application will be listened on port 8088.

2. Use a curl client to request to the http://localhost:8088/test endpoint. 
   The /test endpoint will internally make a GET request to /server. Both endpoints are reside on same server for simplicity.  
   
   | curl http://localhost:8088/test
   
   > // Loggings from first get request, the request is successful received by server:
   > 
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: Handling /test request...
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: Accepting /server request...
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: /server responded.
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: Headers:
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: x-my-header -> my-custom-header
   > Jan 04, 2023 10:53:08 AM com.example.starter.Main
   > INFO: content-length -> 9

3. Run the same curl request to the same endpoint to observe the error:
   
   | curl http://localhost:8088/test
   
   > // Loggings from second get request onwards, error while validating X-B3-TraceId, request is not sent to server. HTTP2 required all headers to be in lower case:
   > 
   > Jan 04, 2023 10:53:13 AM com.example.starter.Main
   > INFO: Handling /test request...
   > Jan 04, 2023 10:53:13 AM com.example.starter.Main
   > INFO: Error while client request
   > io.netty.handler.codec.http2.Http2Exception: invalid header name [X-B3-TraceId]
   > 	at io.netty.handler.codec.http2.Http2Exception.connectionError(Http2Exception.java:109)
   > 	at io.netty.handler.codec.http2.DefaultHttp2Headers$2.validateName(DefaultHttp2Headers.java:71)
   > 	at io.netty.handler.codec.http2.DefaultHttp2Headers$2.validateName(DefaultHttp2Headers.java:42)
   > 	at io.netty.handler.codec.DefaultHeaders.validateName(DefaultHeaders.java:1012)
   > 	at io.netty.handler.codec.http2.DefaultHttp2Headers.validateName(DefaultHttp2Headers.java:166)
   > 	at io.netty.handler.codec.http2.DefaultHttp2Headers.validateName(DefaultHttp2Headers.java:33)
   > 	at io.netty.handler.codec.DefaultHeaders.add(DefaultHeaders.java:329)
   > 	at io.vertx.tracing.opentelemetry.HeadersPropagatorSetter.set(HeadersPropagatorSetter.java:24)
   > 	at io.vertx.tracing.opentelemetry.HeadersPropagatorSetter.set(HeadersPropagatorSetter.java:17)
   > 	at io.opentelemetry.extension.trace.propagation.B3PropagatorInjectorMultipleHeaders.inject(B3PropagatorInjectorMultipleHeaders.java:52)
   > 	at io.opentelemetry.extension.trace.propagation.B3Propagator.inject(B3Propagator.java:111)
   > 	at io.vertx.tracing.opentelemetry.OpenTelemetryTracer.sendRequest(OpenTelemetryTracer.java:130)
   > 	at io.vertx.tracing.opentelemetry.OpenTelemetryTracer.sendRequest(OpenTelemetryTracer.java:31)
   > 	at io.vertx.core.http.impl.Http2ClientConnection$StreamImpl.createStream(Http2ClientConnection.java:570)
   > 	at io.vertx.core.http.impl.Http2ClientConnection$StreamImpl.writeHeaders(Http2ClientConnection.java:533)
   > 	at io.vertx.core.http.impl.Http2ClientConnection$StreamImpl.lambda$writeHead$0(Http2ClientConnection.java:501)
   > 	at io.vertx.core.impl.EventLoopContext.emit(EventLoopContext.java:55)
   > 	at io.vertx.core.impl.ContextBase.emit(ContextBase.java:239)
   > 	at io.vertx.core.http.impl.Http2ClientConnection$StreamImpl.writeHead(Http2ClientConnection.java:500)
   > 	at io.vertx.core.http.impl.Http2UpgradeClientConnection$DelegatingStream.writeHead(Http2UpgradeClientConnection.java:138)
   > 	at io.vertx.core.http.impl.HttpClientRequestImpl.doWrite(HttpClientRequestImpl.java:476)
   > 	at io.vertx.core.http.impl.HttpClientRequestImpl.write(HttpClientRequestImpl.java:449)
   > 	at io.vertx.core.http.impl.HttpClientRequestImpl.end(HttpClientRequestImpl.java:395)
   > 	at io.vertx.core.http.impl.HttpClientRequestImpl.end(HttpClientRequestImpl.java:389)
   > 	at io.vertx.ext.web.client.impl.HttpContext.lambda$handleCreateRequest$5(HttpContext.java:485)
   > 	at io.vertx.core.impl.future.FutureImpl$1.onSuccess(FutureImpl.java:91)
   > 	at io.vertx.core.impl.future.FutureBase.emitSuccess(FutureBase.java:60)
   > 	at io.vertx.core.impl.future.FutureImpl.tryComplete(FutureImpl.java:211)
   > 	at io.vertx.core.impl.future.PromiseImpl.tryComplete(PromiseImpl.java:23)
   > 	at io.vertx.core.Promise.complete(Promise.java:66)
   > 	at io.vertx.ext.web.client.impl.HttpContext.handleSendRequest(HttpContext.java:559)
   > 	at io.vertx.ext.web.client.impl.HttpContext.execute(HttpContext.java:375)
   > 	at io.vertx.ext.web.client.impl.HttpContext.next(HttpContext.java:362)
   > 	at io.vertx.ext.web.client.impl.HttpContext.fire(HttpContext.java:329)
   > 	at io.vertx.ext.web.client.impl.HttpContext.sendRequest(HttpContext.java:232)
   > 	at io.vertx.ext.web.client.impl.HttpContext.lambda$handleCreateRequest$6(HttpContext.java:491)
   > 	at io.vertx.core.impl.future.FutureImpl$3.onSuccess(FutureImpl.java:141)
   > 	at io.vertx.core.impl.future.FutureBase.emitSuccess(FutureBase.java:60)
   > 	at io.vertx.core.impl.future.FutureImpl.addListener(FutureImpl.java:196)
   > 	at io.vertx.core.impl.future.PromiseImpl.addListener(PromiseImpl.java:23)
   > 	at io.vertx.core.impl.future.FutureImpl.onComplete(FutureImpl.java:164)
   > 	at io.vertx.core.impl.future.PromiseImpl.onComplete(PromiseImpl.java:23)
   > 	at io.vertx.ext.web.client.impl.HttpContext.handleCreateRequest(HttpContext.java:489)
   > 	at io.vertx.ext.web.client.impl.HttpContext.execute(HttpContext.java:372)
   > 	at io.vertx.ext.web.client.impl.HttpContext.next(HttpContext.java:362)
   > 	at io.vertx.ext.web.client.impl.HttpContext.fire(HttpContext.java:329)
   > 	at io.vertx.ext.web.client.impl.HttpContext.createRequest(HttpContext.java:220)
   > 	at io.vertx.ext.web.client.impl.HttpContext.handlePrepareRequest(HttpContext.java:418)
   > 	at io.vertx.ext.web.client.impl.HttpContext.execute(HttpContext.java:369)
   > 	at io.vertx.ext.web.client.impl.HttpContext.next(HttpContext.java:362)
   > 	at io.vertx.ext.web.client.impl.HttpContext.fire(HttpContext.java:329)
   > 	at io.vertx.ext.web.client.impl.HttpContext.prepareRequest(HttpContext.java:208)
   > 	at io.vertx.ext.web.client.impl.HttpRequestImpl.send(HttpRequestImpl.java:504)
   > 	at io.vertx.ext.web.client.impl.HttpRequestImpl.send(HttpRequestImpl.java:394)
   > 	at io.vertx.reactivex.ext.web.client.HttpRequest.send(HttpRequest.java:826)
   > 	at io.vertx.reactivex.ext.web.client.HttpRequest.lambda$rxSend$40(HttpRequest.java:842)
   > 	at io.vertx.reactivex.impl.AsyncResultSingle.subscribeActual(AsyncResultSingle.java:45)
   > 	at io.reactivex.Single.subscribe(Single.java:3666)
   > 	at io.reactivex.internal.operators.single.SingleDoOnSuccess.subscribeActual(SingleDoOnSuccess.java:35)
   > 	at io.reactivex.Single.subscribe(Single.java:3666)
   > 	at io.reactivex.internal.operators.single.SingleDoOnError.subscribeActual(SingleDoOnError.java:35)
   > 	at io.reactivex.Single.subscribe(Single.java:3666)
   > 	at io.reactivex.internal.operators.completable.CompletableFromSingle.subscribeActual(CompletableFromSingle.java:29)
   > 	at io.reactivex.Completable.subscribe(Completable.java:2309)
   > 	at io.reactivex.Completable.subscribe(Completable.java:2383)
   > 	at com.example.starter.Main.test(Main.java:73)
   > 	at io.vertx.lang.rx.DelegatingHandler.handle(DelegatingHandler.java:20)
   > 	at io.vertx.ext.web.impl.RouteState.handleContext(RouteState.java:1284)
   > 	at io.vertx.ext.web.impl.RoutingContextImplBase.iterateNext(RoutingContextImplBase.java:177)
   > 	at io.vertx.ext.web.impl.RoutingContextImpl.next(RoutingContextImpl.java:141)
   > 	at io.vertx.ext.web.impl.RouterImpl.handle(RouterImpl.java:68)
   > 	at io.vertx.ext.web.impl.RouterImpl.handle(RouterImpl.java:37)
   > 	at io.vertx.reactivex.ext.web.Router.handle(Router.java:97)
   > 	at io.vertx.reactivex.ext.web.Router.handle(Router.java:51)
   > 	at io.vertx.lang.rx.DelegatingHandler.handle(DelegatingHandler.java:20)
   > 	at io.vertx.core.http.impl.Http1xServerRequestHandler.handle(Http1xServerRequestHandler.java:67)
   > 	at io.vertx.core.http.impl.Http1xServerRequestHandler.handle(Http1xServerRequestHandler.java:30)
   > 	at io.vertx.core.impl.EventLoopContext.emit(EventLoopContext.java:55)
   > 	at io.vertx.core.impl.DuplicatedContext.emit(DuplicatedContext.java:158)
   > 	at io.vertx.core.http.impl.Http1xServerConnection.handleMessage(Http1xServerConnection.java:145)
   > 	at io.vertx.core.net.impl.ConnectionBase.read(ConnectionBase.java:157)
   > 	at io.vertx.core.net.impl.VertxHandler.channelRead(VertxHandler.java:153)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:442)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
   > 	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:93)
   > 	at io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler.channelRead(WebSocketServerExtensionHandler.java:99)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:442)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
   > 	at io.vertx.core.http.impl.Http1xUpgradeToH2CHandler.channelRead(Http1xUpgradeToH2CHandler.java:120)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
   > 	at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:346)
   > 	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:318)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
   > 	at io.vertx.core.http.impl.Http1xOrH2CHandler.end(Http1xOrH2CHandler.java:61)
   > 	at io.vertx.core.http.impl.Http1xOrH2CHandler.channelRead(Http1xOrH2CHandler.java:38)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412)
   > 	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:440)
   > 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420)
   > 	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
   > 	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)
   > 	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:788)
   > 	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:724)
   > 	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:650)
   > 	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562)
   > 	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
   > 	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
   > 	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
   > 	at java.base/java.lang.Thread.run(Thread.java:833)
   > 
   > Jan 04, 2023 10:53:13 AM io.vertx.core.http.impl.HttpClientRequestImpl
   > SEVERE: invalid header name [X-B3-TraceId]
