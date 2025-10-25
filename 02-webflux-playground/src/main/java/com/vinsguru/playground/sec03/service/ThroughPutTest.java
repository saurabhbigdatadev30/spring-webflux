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
      1. This method runs a throughput test by fetching number of customers from the repository using
          their ids.
      2. It uses Flux.range to generate a range of IDs from 1 to TASKS_COUNT .
      3. For each ID, we invoke repository.findById(id), using flatMap .
      4. So repository.findById(id) is called TASKS_COUNT times concurrently, since non-blocking calls are made.
   */
    private void runThroughputTestForR2DBC() {
        Flux.range(1, TASKS_COUNT)
                //.flatMap(id -> this.repository.findById(id))
                .flatMap(this.repository::findById)
                .then()
                .block(); // wait for all the tasks to complete
    }

    /*
     1. measureTimeTaken is High-Order function that takes an iteration number and a Runnable task as parameters.
     2. We program to interface rather than implementation, since we can pass  multiple implementations of Runnable to
           this method.
     */
    private void measureTimeTaken(int iteration, Runnable runnable){
        var start = System.currentTimeMillis();
        runnable.run();
        var timeTaken = (System.currentTimeMillis() - start); // in millis
        var throughput = (1.0 * TASKS_COUNT / timeTaken) * 1000; //  we multiply by 1000 to get throughput in seconds.
        log.info("test: {} - took: {} ms, throughput: {} / sec", iteration , timeTaken, throughput);
    }



    public void execute() {
        var executor = getFixedExecutor();
        int iterations = 10;
        for (int i = 1; i <= iterations; i++) {

            /*
              invoking the high-order function measureTimeTaken with different implementations of Runnable
              Advantage of using High-Order function here is that we can pass different implementations of Runnable
                to measureTimeTaken() method, without changing its code. So the code is more flexible and reusable.
             */
            this.measureTimeTaken(i, this::runThroughputTestForR2DBC);
            this.measureTimeTaken(i , () -> runThroughPutTestForJPA(executor));
        }
    }



    private ExecutorService getFixedExecutor(){
        return Executors.newFixedThreadPool(256); // reactor sends 256 at a time via flatMap
    }

    /*
       The execute method uses an ExecutorService to submit tasks that fetch customers by their IDs
         Each task calls repository.findById(customerID) is executed in a separate thread managed by the ExecutorService.
         The max thread pool size is set to 256 threads. So max 256 threads will be active at a time.
         The 256 threads will be reused for multiple tasks [TASKS_COUNT = 100_000] .
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
