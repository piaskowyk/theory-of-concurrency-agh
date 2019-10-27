package lab.zad1;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer();
        int producerCount = 4;
        int consumerCount = 8;

        ArrayList<Thread> producerThreads = new ArrayList<>();
        ArrayList<Thread> consumerThread = new ArrayList<>();

        //creating
        for(int i = 0; i < producerCount; i++) {
            producerThreads.add(new Thread(new Producer(buffer)));
        }

        for(int i = 0; i < consumerCount; i++) {
            consumerThread.add(new Thread(new Consumer(buffer)));
        }

        //starting
        for(int i = 0; i < producerCount; i++) {
            producerThreads.get(i).start();
        }

        for(int i = 0; i < consumerCount; i++) {
            consumerThread.get(i).start();
        }

        //joining
        for(int i = 0; i < producerCount; i++) {
            producerThreads.get(i).join();
        }

        for(int i = 0; i < consumerCount; i++) {
            consumerThread.get(i).join();
        }
    }
}

class Buffer{
    final Lock lock = new ReentrantLock();
    final Condition notFull  = lock.newCondition();
    final Condition notEmpty = lock.newCondition();

    private String message = "";

    void put(String message) throws InterruptedException {
        lock.lock();
        try {
            while(!this.message.equals("")) {
                notFull.await();
            }
            this.message = message;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    String take() throws InterruptedException {
        lock.lock();
        try {
            while(this.message.equals("")){
                notEmpty.await();
            }

            String copy = this.message;
            this.message = "";
            notFull.signal();

            return copy;
        } finally {
            lock.unlock();
        }
    }
}

class Producer implements Runnable {
    private Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0; i < 100; i++) {
            try {
                buffer.put("message " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

class Consumer implements Runnable {
    private Buffer buffer;

    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0; i < 50; i++) {
            String message = null;
            try {
                message = buffer.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(message);
        }

    }
}