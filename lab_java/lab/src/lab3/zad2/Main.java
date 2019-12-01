package lab3.zad2;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int customersCount = 10;
        PrinterManager printerManager = new PrinterManager(3);
        ArrayList<Thread> customersThread = new ArrayList<>();

        //creating
        for(int i = 0; i < customersCount; i++) {
            customersThread.add(new Thread(new Customer(printerManager, i)));
        }

        //starting
        for(int i = 0; i < customersCount; i++) {
            customersThread.get(i).start();
        }

        //joining
        for(int i = 0; i < customersCount; i++) {
            customersThread.get(i).join();
        }
    }
}

//printer monitor
class PrinterManager {

    private final Lock lock = new ReentrantLock();
    private final Condition fullCondition  = lock.newCondition();

    private boolean[] printersState;
    private int printersCount;

    PrinterManager(int printersCount) {
        this.printersCount = printersCount;
        printersState = new boolean[printersCount];
        for(int i = 0; i < printersCount; i++) {
            printersState[i] = true;
        }
    }

    private int searchFreePrinter() {
        for(int i = 0; i < printersCount; i++) {
            if(printersState[i]) {
                return i;
            }
        }
        return -1;
    }

    int getPrinter() throws InterruptedException {
        lock.lock();
        try {
            int freePrinter = -1;
            while(freePrinter < 0) {
                freePrinter = searchFreePrinter();
                if(freePrinter < 0) {
                    fullCondition.await();
                }
            }
            printersState[freePrinter] = false;

            return freePrinter;
        } finally {
            lock.unlock();
        }

    }

    void releasePrinter(int printerId) {
        lock.lock();
        printersState[printerId] = true;
        fullCondition.signal();
        lock.unlock();
    }

    void print(int printerId, int customerId) {
        System.out.println("Printer " + printerId + " start printing (customer: " + customerId + ")");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Printer " + printerId + " end printing (customer: " + customerId + ")");
    }
}

class Customer implements Runnable {
    private PrinterManager printerManager;
    private int customerId;

    Customer(PrinterManager printerManager, int customerId) {
        this.printerManager = printerManager;
        this.customerId = customerId;
    }

    public void run() {
        int printerId = 0;
        while(true) {
            try {
                printerId = printerManager.getPrinter();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printerManager.print(printerId, customerId);
            printerManager.releasePrinter(printerId);
        }
    }
}