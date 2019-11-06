package lab5.zad2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("START");
        MandelbrotMeasurement mandelbrot = new MandelbrotMeasurement();

        System.out.println("1 Thread");
        mandelbrot.measurement(1, "1 Thread");

        System.out.println("12 Thread");
        mandelbrot.measurement(12, "12 Thread");

        System.out.println("24 Thread");
        mandelbrot.measurement(24, "24 Thread");

        System.out.println("END");
    }
}

class MandelbrotMeasurement {
    private int height = 600;
    private int width = 800;
    private int maxIteration = 1000000;

    void measurement(int threadCount, String measurementName) throws ExecutionException, InterruptedException, IOException {
        Measurement measurement = new Measurement();
        for(int probeIndex = 0; probeIndex < 10; probeIndex++) {
            measurement.startMeasurement();

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            List<Future> futureThreads = new ArrayList<>();
            for(int i = 0; i < threadCount; i++) {
                int finalI = i;
                futureThreads.add(executorService.submit(() -> parallelDraw(finalI, threadCount)));
            }

            for(Future item : futureThreads) {
                item.get();
            }
            executorService.shutdown();

            measurement.endMeasurement();
        }
        measurement.summary(measurementName, threadCount, threadCount);

        //-------------------------------------------------------------------------------------

        measurement = new Measurement();
        for(int probeIndex = 0; probeIndex < 10; probeIndex++) {
            measurement.startMeasurement();

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            List<Future> futureThreads = new ArrayList<>();
            for(int i = 0; i < threadCount * 10; i++) {
                int finalI = i;
                futureThreads.add(executorService.submit(() -> parallelDraw(finalI, threadCount)));
            }

            for(Future item : futureThreads) {
                item.get();
            }
            executorService.shutdown();

            measurement.endMeasurement();
        }
        measurement.summary(measurementName, threadCount, threadCount * 10);

        //-------------------------------------------------------------------------------------

        measurement = new Measurement();
        for(int probeIndex = 0; probeIndex < 10; probeIndex++) {
            measurement.startMeasurement();

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            List<Future> futureThreads = new ArrayList<>();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int finalX = x;
                    int finalY = y;
                    futureThreads.add(executorService.submit(() -> pixelDraw(finalX, finalY)));
                }
            }
            executorService.shutdown();

            for(Future item : futureThreads) {
                item.get();
            }

            measurement.endMeasurement();
        }
        measurement.summary(measurementName, threadCount, width * height);
    }

    private void parallelDraw(int threadId, int threadCount) {
        double zy, zx, cX, cY;
        double ZOOM = 150;

        for (int y = threadId; y < height; y += threadCount) {
            for (int x = 0; x < width; x++) {
                zx = zy = 0;
                cX = (x - 400) / ZOOM;
                cY = (y - 300) / ZOOM;
                while (zx * zx + zy * zy < 4 && maxIteration > 0) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    maxIteration--;
                }
            }
        }
    }

    private void pixelDraw(int x, int y) {
        double zy, zx, cX, cY;
        double ZOOM = 150;

        zx = zy = 0;
        cX = (x - 400) / ZOOM;
        cY = (y - 300) / ZOOM;
        while (zx * zx + zy * zy < 4 && maxIteration > 0) {
            double tmp = zx * zx - zy * zy + cX;
            zy = 2.0 * zx * zy + cY;
            zx = tmp;
            maxIteration--;
        }
    }

}

class Measurement {
    private List<Long> timestampStart = new ArrayList<>();
    private List<Long> timestampEnd = new ArrayList<>();

    void startMeasurement() {
        timestampStart.add(System.nanoTime());
    }

    void endMeasurement() {
        timestampEnd.add(System.nanoTime());
    }

    void summary(String measurementName, int threadPool, int taskCount) throws IOException {
        List<Long> timestampDiff = new ArrayList<>();
        double average, standardDeviation;
        FileWriter writer = new FileWriter("./src/lab5/zad2/measurement", true);

        Double measurementCount = 10D;
        for(int i = 0; i < measurementCount; i++) {
            timestampDiff.add(timestampEnd.get(i) - timestampStart.get(i));
        }

        average = timestampDiff.stream().reduce(0L, Long::sum) / measurementCount;
        standardDeviation = Math.sqrt(
            timestampDiff
                    .stream()
                    .reduce(0L,
                        (accumulator, element) -> (long)(accumulator + Math.pow(element - average, 2))
                    ) / measurementCount
        );

        writer.append(measurementName)
                .append("\n")
                .append("threadPool: ")
                .append(String.valueOf(threadPool))
                .append(" taskCount: ")
                .append(String.valueOf(taskCount))
                .append(" avg: ")
                .append(String.valueOf(average / 1000000000))
                .append("s std: ")
                .append(String.valueOf(standardDeviation / 1000000000))
                .append("s\n\n");

        writer.close();
    }
}