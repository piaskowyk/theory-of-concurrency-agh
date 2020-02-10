package lab4.zad2C;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Main_C {
    public static void main(String[] args) throws InterruptedException, IOException {
//        int[] lineSizeSet = {1000, 10000, 100000};
//        int[] processCountSizeSet = {10, 100, 1000};

        int lineSize = 10000;
        int processCount = 100;
        int measurementTime = 30;

        if(args.length >= 2) {
            lineSize = Integer.parseInt(args[0]);
            processCount = Integer.parseInt(args[1]);
        }
        Measurement.init(processCount, lineSize);

        Line line = new Line(lineSize, processCount);

        //creating
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        for(int i = 0; i < processCount; i++) {
            producers.add(new Thread(new Producer(line, i)));
            consumers.add(new Thread(new Consumer(line, i)));
        }

        //starting
        for(int i = 0; i < processCount; i++) {
            producers.get(i).start();
            consumers.get(i).start();
        }
        sleep(1000 * measurementTime);

        Measurement.summary();

        System.exit(0);
    }
}

class Producer implements Runnable {
    private Line line;
    private int producerId;

    Producer(Line line, int producerId) {
        this.line = line;
        this.producerId = producerId;
    }

    @Override
    public void run() {
        while(true) {
            line.produce(producerId);
        }
    }
}

class Consumer implements Runnable {
    private Line line;
    private int consumerId;

    Consumer(Line line, int consumerId) {
        this.line = line;
        this.consumerId = consumerId;
    }

    @Override
    public void run() {
        while(true) {
            line.consume(consumerId);
        }
    }
}

class Line {
    private int lineSize;
    private int[] line;
    private int processCount;
    private Random generator = new Random();
    private final Lock lock = new ReentrantLock();

    private List<Condition> producerCondition = new ArrayList<>();
    private List<Condition> consumerCondition = new ArrayList<>();
    private int producerPortionIndex = 0;
    private int consumerPortionIndex = 0;

    Line(int lineSize, int processCount) {
        this.lineSize = lineSize;
        line = new int[lineSize];
        this.processCount = processCount;

        for(int i = 0; i < processCount; i++) {
            producerCondition.add(lock.newCondition());
            consumerCondition.add(lock.newCondition());
        }

    }

    private void schedulerProduce(int selfId) {
        do {
            producerPortionIndex = (producerPortionIndex + 1) % processCount;
        } while(selfId == producerPortionIndex);

        producerCondition.get(producerPortionIndex).signal();
    }

    private void schedulerConsume(int selfId) {
        do {
            consumerPortionIndex = (consumerPortionIndex + 1) % processCount;
        } while(selfId == consumerPortionIndex);

        consumerCondition.get(consumerPortionIndex).signal();
    }

    void produce(int producerId) {
        int portionSize = generator.nextInt((lineSize / 2) - 1) + 1;
        long timestamp;
        lock.lock();
        try {
            timestamp = System.nanoTime();
            while(!searchSpace(portionSize)) {
                schedulerProduce(producerId);
                schedulerConsume(-1);

                producerCondition.get(producerId).await();
            }
            Measurement.addMeasurementProducer(System.nanoTime() - timestamp, portionSize);
//            System.out.println("Producer (" + producerId + ") produce " + portionSize);
            producePortion(portionSize);
//            printLine();
            schedulerConsume(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void consume(int consumerId) {
        int portionSize = generator.nextInt((lineSize / 2) - 1) + 1;
        long timestamp;
        lock.lock();
        try {
            timestamp = System.nanoTime();
            while(!searchProduct(portionSize)) {
                schedulerProduce(-1);
                schedulerConsume(consumerId);

                consumerCondition.get(consumerId).await();
            }
            Measurement.addMeasurementConsumer(System.nanoTime() - timestamp, portionSize);
//            System.out.println("Consumer (" + consumerId + ") consume " + portionSize);
            consumePortion(portionSize);
//            printLine();
            schedulerProduce(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private boolean searchProduct(int count) {
        int counter = 0;
        for(int item : line) {
            if(item != 1) return false;
            counter++;
            if(counter == count) return true;
        }
        return false;
    }

    private boolean searchSpace(int count) {
        int counter = 0;
        for(int item : line) {
            if(item == 0) {
                counter++;
                if(counter == count) return true;
            }
        }
        return false;
    }

    private void consumePortion(int portionSize) {
        for(int i = lineSize - 1; portionSize > 0; i--){
            if(line[i] == 1) {
                portionSize--;
                line[i] = 0;
            }
        }
    }

    private void producePortion(int portionSize) {
        for(int i = 0; portionSize > 0; i++) {
            if(line[i] == 0) {
                portionSize--;
                line[i] = 1;
            }
        }
    }

    private void printLine() {
        System.out.print("|");
        for(int item : line) {
            System.out.print(item + "|");
        }
        System.out.println();
    }
}

class Measurement {
    private static double[] measurementCountProducers;
    private static double[] timeSumProducers;
    private static double[] measurementCountConsumers;
    private static double[] timeSumConsumers;
    private static int processCount;
    private static int lineSize;

    static void init(int _processCount, int _lineSize) {
        processCount = _processCount;
        lineSize = _lineSize;
        measurementCountProducers = new double[lineSize/2];
        timeSumProducers = new double[lineSize/2];
        measurementCountConsumers = new double[lineSize/2];
        timeSumConsumers = new double[lineSize/2];
    }

    static synchronized void addMeasurementProducer(double timeDiff, int portionSize) {
        measurementCountProducers[portionSize]++;
        timeSumProducers[portionSize] += timeDiff;
    }

    static synchronized void addMeasurementConsumer(double timeDiff, int portionSize) {
        measurementCountConsumers[portionSize]++;
        timeSumConsumers[portionSize] += timeDiff;
    }

    static synchronized void summary() throws IOException {
        FileWriter writer = new FileWriter(
                "./../data/" + lineSize + "_" + processCount + "_P"
        );

        for(int i = 1; i < lineSize / 2; i++) {
            if(measurementCountProducers[i] == 0) {
                writer.append(String.valueOf(i)).append(" ")
                        .append(String.valueOf(0))
                        .append("\n");
            }
            else {
                writer.append(String.valueOf(i)).append(" ")
                        .append(String.valueOf(timeSumProducers[i] / measurementCountProducers[i]))
                        .append("\n");
            }
        }
        writer.close();

        writer = new FileWriter(
                        "./../data/" + lineSize + "_" + processCount + "_C"
                );

        for(int i = 1; i < lineSize / 2; i++) {
            if(measurementCountConsumers[i] == 0) {
                writer.append(String.valueOf(i)).append(" ")
                        .append(String.valueOf(0))
                        .append("\n");
            }
            else {
                writer.append(String.valueOf(i)).append(" ")
                        .append(String.valueOf(timeSumConsumers[i] / measurementCountConsumers[i]))
                        .append("\n");
            }
        }

        writer.close();

        writer = new FileWriter(
                "./../data/summary",
                true
                );

        double operation = 0;
        double sumTime = 0;
        for(int i = 0; i < lineSize / 2; i++) {
            operation += measurementCountConsumers[i];
            sumTime += timeSumConsumers[i];
        }

        writer.append(String.valueOf(lineSize))
                .append("_")
                .append(String.valueOf(processCount))
                .append("_C operation: ")
                .append(String.valueOf(operation))
                .append(" time: ")
                .append(String.valueOf(sumTime))
                .append(" average: ")
                .append(String.valueOf(sumTime / operation))
                .append("\n");

        for(int i = 0; i < lineSize / 2; i++) {
            operation += measurementCountProducers[i];
            sumTime += timeSumProducers[i];
            String.valueOf(sumTime);
        }

        writer.append(String.valueOf(lineSize))
                .append("_")
                .append(String.valueOf(processCount))
                .append("_P operation: ")
                .append(String.valueOf(operation))
                .append(" time: ")
                .append(String.valueOf(sumTime))
                .append(" average: ")
                .append(String.valueOf(sumTime / operation))
                .append("\n");

        writer.close();
    }
}