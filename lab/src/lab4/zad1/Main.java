package lab4.zad1;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int processorCount = 3;

        Line line = new Line(10);

        //creating
        Thread producer = new Thread(new Producer(line));
        Thread consumer = new Thread(new Consumer(line));
        List<Thread> processors = new ArrayList<>();
        for(int i = 0; i < processorCount; i++) {
            processors.add(new Thread(new Processor(line, i)));
        }

        //starting
        producer.start();
        consumer.start();
        for(int i = 0; i < processorCount; i++) {
            processors.get(i).start();
        }

        //joining
        producer.join();
        consumer.join();
        for(int i = 0; i < processorCount; i++) {
            processors.get(i).join();
        }
    }
}

class Producer implements Runnable {
    private Line line;

    Producer(Line line) {
        this.line = line;
    }

    @Override
    public void run() {
        while(true) {
            line.produce();
        }
    }
}

class Processor implements Runnable {
    private Line line;
    private int processorId;

    Processor(Line line, int processorId) {
        this.line = line;
        this.processorId = processorId;
    }

    @Override
    public void run() {
        line.process(this.processorId);
    }
}

class Consumer implements Runnable {
    private Line line;

    Consumer(Line line) {
        this.line = line;
    }

    @Override
    public void run() {
        line.consume();
    }
}

class Line {
    private int lineSize;
    private int[] line;
    private int producerIndex = 0;
    private int consumerIndex = 0;
    private int[] processorIndex;

    Line(int lineSize) {
        this.lineSize = lineSize;
        line = new int[lineSize];
        for(int i = 0; i < lineSize; i++) {
            line[i] = -1;
        }

        processorIndex = new int[lineSize];
    }

    public void produce() {

    }

    public void process(int processorId) {

    }

    public void consume() {

    }
}