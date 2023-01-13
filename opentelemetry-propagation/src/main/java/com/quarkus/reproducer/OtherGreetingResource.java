package com.quarkus.reproducer;

import io.opentelemetry.api.trace.Span;
import io.quarkus.bootstrap.forkjoin.QuarkusForkJoinWorkerThreadFactory;
import io.smallrye.context.api.ManagedExecutorConfig;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Path("/hola")
public class OtherGreetingResource {

    private ExecutorService myExecutorService;

    private MyAsyncService myAsyncService;

    private ThreadContext threadContext;

    @Inject
    public OtherGreetingResource(ThreadContext threadContext, MyAsyncService myAsyncService,
        @ManagedExecutorConfig(maxAsync = 10, maxQueued = 3, cleared = ThreadContext.ALL_REMAINING)
        ManagedExecutor executor) {
        this.myExecutorService = executor;
        this.myAsyncService = myAsyncService;
        this.threadContext = threadContext;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> hello() {
        return this.threadContext.withContextCapture(this.myAsyncService.runService("Hello " + Span.current().getSpanContext().getTraceId(), this.myExecutorService))
            .thenApplyAsync(message -> message + "-" + Span.current().getSpanContext().getTraceId(), this.myExecutorService);
    }
}