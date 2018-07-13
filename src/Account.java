import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private int balance;
    private AtomicInteger failCounter = new AtomicInteger(0);
    private Lock lock = new ReentrantLock();

    public Account(int initialBalance) {
        this.balance = initialBalance;
    }

    public void withdraw(int amount) {
        this.balance -= amount;
    }

    public void deposit(int amount) {
        this.balance += amount;
    }

    public void incFailedTransferCount() {
        this.failCounter.incrementAndGet();
    }

    public int getFailCounter() {
        return failCounter.get();
    }

    public Lock getLock() {
        return lock;
    }

    public int getBalance() {
        return balance;
    }

}
