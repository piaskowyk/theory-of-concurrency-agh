package lab3.zad3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.getAllStackTraces;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int clientsCount = 10;

        Waiter waiter = new Waiter(clientsCount);
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
    private int pairIdWithTable;
    private boolean tableIsFree = true;
    private final Lock lock = new ReentrantLock();
    private ArrayList<Condition> pairCondition = new ArrayList<>();
    private Condition tableCondition = lock.newCondition();
    private int[] pairReservationState;

    Waiter(int clientsCount) {
        int pairCount = clientsCount / 2;
        for(int i = 0; i < pairCount; i++) {
            pairCondition.add(lock.newCondition());
        }
        pairReservationState = new int[pairCount];
    }

    public void getTable(int pairId) {
        lock.lock();
        try {
            pairReservationState[pairId]++;

            if(pairReservationState[pairId] != 2) {
                while(pairReservationState[pairId] != 2) {
                    pairCondition.get(pairId).await();
                }
            }
            else {
                while(!tableIsFree) {
                    tableCondition.await();
                }
                pairIdWithTable = pairId;
                tableIsFree = false;

                pairCondition.get(pairId).signal();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void releaseTable() {
        lock.lock();
        pairReservationState[pairIdWithTable]--;
        if(pairReservationState[pairIdWithTable] == 0) {
            tableIsFree = true;
            pairIdWithTable = -1;
            tableCondition.signalAll();
        }
        lock.unlock();
    }
}

class Client implements Runnable {
    private int clientId;
    private int pairId;
    private Random random = new Random();
    private Waiter waiter;

    Client(Waiter waiter, int clientId) {
        this.clientId = clientId;
        this.pairId = clientId / 2;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        while(true) {
            try {
                //do somethings
                sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Client (" + clientId + ", " + pairId + ") want table.");
            waiter.getTable(pairId);
            try {
                //eat
                System.out.println("Client (" + clientId + ", " + pairId + ") start eating.");
                sleep(random.nextInt(5000));
                System.out.println("Client (" + clientId + ", " + pairId + ") end eating.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Client (" + clientId + ", " + pairId + ") release table.");
            waiter.releaseTable();
        }
    }
}