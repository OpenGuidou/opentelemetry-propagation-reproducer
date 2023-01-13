package com.quarkus.reproducer;

import io.opentracing.util.GlobalTracer;
import io.quarkus.bootstrap.forkjoin.QuarkusForkJoinWorkerThreadFactory;
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

@Path("/hello")
public class GreetingResource {

    private ExecutorService myExecutorService;

    private MyAsyncService myAsyncService;

    private ThreadContext threadContext;

    @Inject
    public GreetingResource(ThreadContext threadContext, MyAsyncService myAsyncService) {
        this.myExecutorService = createNewExecutor(32, "myThreadPool");
        this.myAsyncService = myAsyncService;
        this.threadContext = threadContext;
    }

    public static ForkJoinPool createNewExecutor(int poolSize, String poolName) {
        return new ForkJoinPool(poolSize,
            new QuarkusForkJoinWorkerThreadFactory(), null, true,
            0, 0x7fff, 1, null, 500,
            TimeUnit.MILLISECONDS);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> hello() {
        return this.threadContext.withContextCapture(this.myAsyncService.runService("Hello " + GlobalTracer.get().activeSpan().context().toTraceId(), this.myExecutorService))
            .thenApplyAsync(message -> message + "-" + GlobalTracer.get().activeSpan().context().toTraceId(), this.myExecutorService);
    }
}