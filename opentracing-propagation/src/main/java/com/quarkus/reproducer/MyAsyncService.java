package com.quarkus.reproducer;

import io.opentracing.util.GlobalTracer;
import org.eclipse.microprofile.context.ThreadContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

@ApplicationScoped
public class MyAsyncService {

  private ThreadContext threadContext;

  public MyAsyncService(ThreadContext threadContext) {
    this.threadContext = threadContext;
  }


  public CompletionStage<String> runService(String greeting, ExecutorService executorService) {

    return this.threadContext.withContextCapture(CompletableFuture.supplyAsync(() -> greeting, executorService))
        .thenApplyAsync(message -> message + "-" + GlobalTracer.get().activeSpan().context().toTraceId(), executorService);

  }


}
