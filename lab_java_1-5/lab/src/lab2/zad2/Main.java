package lab2.zad2;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int clientCount = 10;
        Shop shop = new Shop(3);
        ArrayList<Thread> clientThread = new ArrayList<>();

        for(int i = 0; i < clientCount; i++) {
            clientThread.add(new Thread(new Client(shop, i)));
        }

        for(int i = 0; i < clientCount; i++) {
            clientThread.get(i).start();
        }

        for(int i = 0; i < clientCount; i++) {
            clientThread.get(i).join();
        }
    }
}

class Shop {

    private int chartCount;

    Shop(int chartCount) {
        this.chartCount = chartCount;
    }


    synchronized void getChart() {
        while(chartCount <= 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        chartCount--;
    }

    synchronized void releaseChart() {
        chartCount++;
        notifyAll();
    }
}

class Client implements Runnable {
    private Shop shop;
    private int clientId;

    Client(Shop shop, int clientId) {
        this.shop = shop;
        this.clientId = clientId;
    }

    public void run() {
        shop.getChart();
        for(int i = 0; i < 5; i++) {
            System.out.println("Client (" + clientId + ") doing " + i);

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        shop.releaseChart();
    }
}