import javax.naming.InsufficientResourcesException;

public class Operations {

    public static void main(String[] args) throws InsufficientResourcesException {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        new Thread(() -> {
            try {
                transfer(a, b, 500);
            } catch (InsufficientResourcesException e) {
                e.printStackTrace();
            }
        }).start();
        transfer(b, a, 300);
    }

    static void transfer(Account acc1, Account acc2, int amount) throws InsufficientResourcesException {
        if (acc1.getBalance() < amount) {
            throw new InsufficientResourcesException();
        }
        if (acc1.getLock().tryLock()) {
            try {
                System.out.println(Thread.currentThread().getName() + ": sync1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (acc2.getLock().tryLock()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": sync2");
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                        System.out.println("transfer access");
                    } finally {
                        acc2.getLock().unlock();
                    }
                } else {
                    System.out.println("error sync 2");
                    acc2.incFailedTransferCount();
                }
            } finally {
                acc1.getLock().unlock();
            }
        } else {
            System.out.println("error sync 1");
            acc1.incFailedTransferCount();
        }
    }

}
