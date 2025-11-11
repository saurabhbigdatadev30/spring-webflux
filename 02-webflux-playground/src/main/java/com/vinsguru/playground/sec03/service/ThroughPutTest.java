package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThroughPutTest {
    private static final int TASKS_COUNT = 100_000;

    @Autowired
    private CustomerRepository repository;

    @Value("${useVirtualThreadExecutor:false}")
    private boolean useVirtualThreadExecutor;

 /*
   This is to demo that when we use map instead of flatMap, since map returns a Flux of Mono<Customer>,
   The inner Monos are not subscribed to, so the calls to repository.findById(id)
   */
    private void runThroughputTestForR2DBCWithMap() {
        Flux.range(1, TASKS_COUNT)
                .map(this.repository::findById)
                .then()
                .block(); // wait for all the tasks to complete
    }


    private void runThroughputTestForR2DBC() {
        Flux.range(1, TASKS_COUNT)
                //.flatMap(id -> this.repository.findById(id)) Use method reference instead
                .flatMap(this.repository::findById)
                .then()
                .block(); // wait for all the tasks to complete, else the method will return immediately
    }

    /*
     1. measureTimeTaken is High-Order function that takes an iteration number and a Runnable task as parameters.
     2. We program to interface rather than implementation, since we can pass  multiple implementations of Runnable to
           this method.
     */
    private void measureTimeTaken(int iteration, Runnable runnable){
        var start = System.currentTimeMillis();
        runnable.run();
        var totalTimeElapsed = (System.currentTimeMillis() - start); // in millis
        var throughput = (1.0 * TASKS_COUNT / totalTimeElapsed) * 1000; //  we multiply by 1000 to get throughput in seconds.
        log.info("test: {} - took: {} ms, throughput: {} / sec", iteration , totalTimeElapsed, throughput);
    }

    public void execute() {
        var executorService = getFixedExecutor();
        int iterations = 10;
        for (int i = 1; i <= iterations; i++) {

            /*
              invoking the high-order function measureTimeTaken with different implementations of Runnable
              Advantage of using High-Order function here is that we can pass different implementations of Runnable
              to measureTimeTaken() method, without changing its code. So the code is more flexible and reusable.
             */
            this.measureTimeTaken(i, this::runThroughputTestForR2DBC);
            this.measureTimeTaken(i , () -> runThroughPutTestForJPA(executorService));
        }
    }



    private ExecutorService getFixedExecutor(){
        return Executors.newFixedThreadPool(256); // reactor sends 256 at a time via flatMap
    }

    /*
       1. 100,000 tasks are submitted to a fixed pool of 256 worker threads.
       2. No new thread is created per task. The pool creates up to 256 workers and reuses them.
       3. At most 256 tasks run concurrently; the rest wait in the queue.
       4. The selected call runs on a pooled worker thread, not a newly created thread.
       5. This limits resource usage and context switching overhead compared to creating a new thread per task.
     */
  public void runThroughPutTestForJPA(ExecutorService service){
        for(int i =0 ; i < TASKS_COUNT ; i++){
            final var customerID = i;
            service.submit(() -> {
                 this.repository.findById(customerID);
             });
        }
  }



}
