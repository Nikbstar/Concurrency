import javax.naming.InsufficientResourcesException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Transfer implements Callable<Boolean> {

    private final Account accountFrom;
    private final Account accountTo;
    private final int amount;
    private final  CountDownLatch cdl;

    private static final int WAIT_TIME = 3;

    public Transfer(Account accountFrom, Account accountTo, int amount) {
        this(accountFrom, accountTo, amount, null);
    }

    public Transfer(Account accountFrom, Account accountTo, int amount, CountDownLatch cdl) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.cdl = cdl;
    }

    @Override
    public Boolean call() throws Exception {
        String threadName = Thread.currentThread().getName();
        if (cdl != null) {
            System.out.println(threadName + ": Waiting to start");
            cdl.await();
        }
        boolean result = true;
        if (accountFrom.getLock().tryLock(WAIT_TIME, TimeUnit.SECONDS)) {
            try {
                System.out.println(threadName + ": sync1");
                if (accountFrom.getBalance() < amount) {
                    throw new InsufficientResourcesException();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (accountTo.getLock().tryLock(WAIT_TIME, TimeUnit.SECONDS)) {
                    try {
                        System.out.println(threadName + ": sync2");
                        accountFrom.withdraw(amount);
                        accountTo.deposit(amount);
                        Thread.sleep(new Random().nextInt(5000));
                        System.out.println(threadName + ": transfer access");
                    } finally {
                        accountTo.getLock().unlock();
                    }
                } else {
                    System.out.println(threadName + ": error sync 2");
                    accountTo.incFailedTransferCount();
                    result = false;
                }
            } finally {
                accountFrom.getLock().unlock();
            }
        } else {
            System.out.println(threadName + ": error sync 1");
            accountFrom.incFailedTransferCount();
            result = false;
        }
        return result;
    }

}
