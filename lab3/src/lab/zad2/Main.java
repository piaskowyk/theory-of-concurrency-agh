package lab.zad2;

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
            customersThread.add(new Thread(new Customer(printerManager)));
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
    final Condition fullCondition  = lock.newCondition();
    final Condition freeCondidtion = lock.newCondition();

    private boolean[] printersState;
    private int printersCount;

    PrinterManager(int printersCount) {
        this.printersCount = printersCount;
        printersState = new boolean[printersCount];
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

            return freePrinter;
        } finally {
            lock.unlock();
        }

    }

    void releasePrinter(int printerId) {
        printersState[printerId] = false;
    }

    void print(int printerId) {
        System.out.println("Printer " + printerId + " start printing.");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Printer " + printerId + " end printing.");
    }
}

class Customer implements Runnable {
    private PrinterManager printerManager;

    Customer(PrinterManager printerManager) {
        this.printerManager = printerManager;
    }

    public void run() {
        int printerId = 0;
        try {
            printerId = printerManager.getPrinter();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printerManager.print(printerId);
        printerManager.releasePrinter(printerId);
    }
}