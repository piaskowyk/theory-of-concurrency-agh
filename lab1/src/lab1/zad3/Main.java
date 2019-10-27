package lab1.zad3;


import java.util.ArrayList;

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
    private String message = "";

    synchronized void put(String message) {
        while(!this.message.equals("")){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.message = message;
        notifyAll();
    }

    synchronized String take() {
        while(this.message.equals("")){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String copy = this.message;
        this.message = "";
        notifyAll();

        return copy;
    }
}

class Producer implements Runnable {
    private Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0; i < 10000; i++) {
            buffer.put("message " + i);
        }

    }
}

class Consumer implements Runnable {
    private Buffer buffer;

    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {

        for(int i = 0; i < 5000; i++) {
            String message = buffer.take();
            System.out.println(message);
        }

    }
}