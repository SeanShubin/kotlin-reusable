package com.seanshubin.kotlin.reusable.di.contract;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface HttpClientContract extends AutoCloseable {
    Optional<CookieHandler> cookieHandler();
    Optional<Duration> connectTimeout();
    HttpClient.Redirect followRedirects();
    Optional<ProxySelector> proxy();
    SSLContext sslContext();
    SSLParameters sslParameters();
    Optional<Authenticator> authenticator();
    HttpClient.Version version();
    Optional<Executor> executor();
    <T> HttpResponse<T>
    send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException;
    <T> CompletableFuture<HttpResponse<T>>
    sendAsync(HttpRequest request,
              HttpResponse.BodyHandler<T> responseBodyHandler);
    <T> CompletableFuture<HttpResponse<T>>
    sendAsync(HttpRequest request,
              HttpResponse.BodyHandler<T> responseBodyHandler,
              HttpResponse.PushPromiseHandler<T> pushPromiseHandler);

    WebSocket.Builder newWebSocketBuilder();
    void shutdown();
    boolean awaitTermination(Duration duration) throws InterruptedException;
    boolean isTerminated();
    void shutdownNow();
}
