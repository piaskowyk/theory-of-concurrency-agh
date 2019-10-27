package lab3.zad3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int clientsCount = 10;

        Waiter waiter = new Waiter();
        ArrayList<Thread> customersThread = new ArrayList<>();

        //creating
        for(int i = 0; i < clientsCount; i++) {
            customersThread.add(new Thread(new Client(waiter, i)));
        }

        //starting
        for(int i = 0; i < clientsCount; i++) {
            customersThread.get(i).start();
        }

        //joining
        for(int i = 0; i < clientsCount; i++) {
            customersThread.get(i).join();
        }
    }
}

class Waiter {
    private int tableCount;
    private Table table = new Table();

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void getTable() {

    }

    public void releaseTable() {

    }
}

class Client implements Runnable {
    private int clientId;
    private Random random = new Random();
    private Waiter waiter;

    Client(Waiter waiter, int clientId) {
        this.clientId = clientId;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        while(true) {
            try {
                //do somethings
                sleep(random.nextInt() % 1000 + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Client (" + clientId + ") want table.");
            waiter.getTable();
            try {
                //eat
                System.out.println("Client (" + clientId + ") start eating.");
                sleep(random.nextInt() % 1000 + 1);
                System.out.println("Client (" + clientId + ") end eating.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waiter.releaseTable();
            System.out.println("Client (" + clientId + ") release table.");
        }
    }
}

class Table {
    private int freePlace = 2;

    public void getPlace() throws Exception {
        if(freePlace > 0) {
            freePlace--;
        }
        else {
            throw new Exception("Table is full.");
        }
    }

    public void releasePlace() throws Exception {
        if(freePlace < 2) {
            freePlace++;
        }
        else {
            throw new Exception("Table has only 2 free place.");
        }
    }

    public boolean isFree() {
        if(freePlace > 0) return true;
        return false;
    }

    public int getFreePlace() {
        return freePlace;
    }
}