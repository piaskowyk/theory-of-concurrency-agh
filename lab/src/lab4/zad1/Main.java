package lab4.zad1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int processorCount = 3;

        Line line = new Line(10, processorCount);

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
        while(true) {
            line.process(this.processorId);
        }
    }
}

class Consumer implements Runnable {
    private Line line;

    Consumer(Line line) {
        this.line = line;
    }

    @Override
    public void run() {
        while(true) {
            line.consume();
        }
    }
}

class Line {
    private int lineSize;
    private int[] line;
    private int producerIndex = 0;
    private int consumerIndex = 0;
    private int[] processorIndex;
    private int processorCount;

    private List<Lock> cellLock = new ArrayList<>();
    private List<Condition> cellCondition = new ArrayList<>();

    Line(int lineSize, int processorCount) {
        this.lineSize = lineSize;
        line = new int[lineSize];
        for(int i = 0; i < lineSize; i++) {
            line[i] = -1;
            cellLock.add(new ReentrantLock());
            cellCondition.add(cellLock.get(i).newCondition());
        }
        this.processorCount = processorCount;
        processorIndex = new int[processorCount];
    }

    void produce() {
        cellLock.get(producerIndex).lock();
        try {
            while(line[producerIndex] != -1) {
                cellCondition.get(producerIndex).await();
            }
            line[producerIndex] = 0;
            System.out.println("L[" + line[producerIndex] + "], Produce at: " + producerIndex);
//            printLine();
            cellCondition.get(producerIndex).signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            int lastIndex = producerIndex;
            incrementProduceIndex();
            cellLock.get(lastIndex).unlock();
        }
    }

    void process(int processorId) {
        int currentProcessorIndex = processorIndex[processorId];
        cellLock.get(currentProcessorIndex).lock();
        try {
            while(line[currentProcessorIndex] != processorId) {
                cellCondition.get(currentProcessorIndex).await();
            }
            line[currentProcessorIndex] = processorId + 1;
            System.out.println("L[" + line[currentProcessorIndex] + "], Processor (" + (processorId + 1) + ") at: " + currentProcessorIndex);
//            printLine();
            cellCondition.get(currentProcessorIndex).signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            int lastIndex = currentProcessorIndex;
            incrementProcessorIndex(processorId);
            cellLock.get(lastIndex).unlock();
        }
    }

    void consume() {
        cellLock.get(consumerIndex).lock();
        try {
            while(line[consumerIndex] != processorCount) {
                cellCondition.get(consumerIndex).await();
            }
            line[consumerIndex] = -1;
            System.out.println("L[" + line[consumerIndex] + "], Consume at: " + consumerIndex);
//            printLine();
            cellCondition.get(consumerIndex).signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            int lastIndex = consumerIndex;
            incrementConsumerIndex();
            cellLock.get(lastIndex).unlock();
        }
    }

    private void incrementProduceIndex() {
        producerIndex = (producerIndex + 1) % lineSize;
    }

    private void incrementConsumerIndex() {
        consumerIndex = (consumerIndex + 1) % lineSize;
    }

    private void incrementProcessorIndex(int processorId) {
        processorIndex[processorId] = (processorIndex[processorId] + 1) % lineSize;
    }

    private void printLine() {
        System.out.print("|");
        for(int item : line) {
            if(item >= 0) {
                System.out.print(" " + item + "|");
            }
            else {
                System.out.print(item + "|");
            }
        }
        System.out.println();
    }
}