import java.util.Random;
import java.util.concurrent.*;

public class Operations2 {

    public static void main(String[] args) throws InterruptedException {

        final Random rnd = new Random();

        final Account a = new Account(2000);
        final Account b = new Account(0);

        CountDownLatch cdl = new CountDownLatch(10);

        ExecutorService service = Executors.newFixedThreadPool(3);
        ScheduledExecutorService failMonitor = Executors.newScheduledThreadPool(1);

        for (int i = 0; i < 10; i++) {
            service.submit(new Transfer(a, b, rnd.nextInt(400), cdl));
            cdl.countDown();
        }

        failMonitor.scheduleAtFixedRate(
                () -> System.out.println("Fail Counter: " + (a.getFailCounter() + b.getFailCounter())),
                2,
                1,
                TimeUnit.SECONDS
        );

        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        System.out.println("A: " + a.getBalance());
        System.out.println("B: " + b.getBalance());
        System.out.println("Summ: " + (a.getBalance() + b.getBalance()));
        failMonitor.shutdown();
    }

}
