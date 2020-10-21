package cl.ucn.disc.hpc.primes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
  1. Escribir una funcion que retorne true/false si un numero es primo
  2. Contar la cantidad de numeros primos que existen entre 2 y 10000
  3. Escribir el codigo que resuelva el punto 2 utilizando 1,2,3 etc nucleos
*/
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);


    private static class PrimeTask implements Runnable {

        private final long number;

        //Provee acceso uniforme a los hilos mediante esta variable
        private final static AtomicInteger counter = new AtomicInteger(0);


        public static int getPrimes(){
        return counter.get();
        }


        public PrimeTask(final Long number){
            this.number = number;
        }

        @Override
        public void run() {
          if(isPrime(this.number)){

              //Aumenta el contador
              counter.getAndIncrement();

          }
        }


        public static final boolean isPrime(final long n){

            //Solo numeros positivos
            if(n<= 0){
                throw new IllegalArgumentException("cant process negativa value");
            }

            //Uno no es primo
            if(n<= 1){
                return false;
            }
            //Probando todos los numeros
            for(long i = 2; i < n ; i++){

                if(n % i == 0){
                    return false;
                }
            }

            return true;

        }

        /*
            //Codigo que utilize la primera vez(Ignorar)

             int a = Integer.parseInt(Thread.currentThread().getName());
                long start = System.currentTimeMillis();

                long nPrimes = 0;
                for(long i = a; i < a+24999;i++){
                    if(isPrime(i)){
                        nPrimes++;
                        //System.out.println(i + "  is Prime");
                    }
                    Integer.parseInt(Thread.currentThread().getName());
                }

                long time = System.currentTimeMillis() - start;


                System.out.println("Prime" + nPrimes + "in" + time  + "ms.");

            System.out.println(a);
        }
        */






    }

    public static void main(String[] args) throws  InterruptedException{

        // Metodo que utilize la primera vez(Ignorar)
        /*
        Thread t1 = new Thread(new ParallelTask(), "1");
        Thread t2 = new Thread(new ParallelTask(), "25000");
        Thread t3 = new Thread(new ParallelTask(), "50000");
        Thread t4 = new Thread(new ParallelTask(), "75000");

        //        t1.start();
        //        t2.start();
        //        t3.start();
        //        t4.start();

        */

        //Cantidad numerica en la cual se van a buscar los numero primos
        final long MAX = 1000000;

        // Herramienta que sirve para tomar el tiempo que demora un proceso o funcion
        final StopWatch stopWatch = StopWatch.createStarted();

        log.debug("Starting the main");

        //Se declara el ejecutor y la cantidad de hilos que se utilizaran
        final ExecutorService executorService = Executors.newFixedThreadPool(6);
        //Ciclo que busca los numeros primos en la cantidad total declarada anteriormente pasandoselos al ejecutor
        for(long i = 1 ;i < MAX ; i++){
            //Se le da la tarea a cada hilo mediante el ejecutor
            executorService.submit(new PrimeTask(i));
        }
        //El ejecutor no recibe mas tareas
        executorService.shutdown();

        //Espera un tiempo a que terminen todos los hilos
        if(executorService.awaitTermination(1, TimeUnit.HOURS)){
            log.debug("Primes founded {} in {}",PrimeTask.getPrimes(),stopWatch);
        }
        else{
            //entrega el tiempo del StopWatch declarado ateriomente
            log.debug("Done in {}", stopWatch);
        }




    }



}
