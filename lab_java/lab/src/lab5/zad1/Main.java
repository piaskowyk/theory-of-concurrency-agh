package lab5.zad1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.JFrame;


public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Mandelbrot mandelbrot = new Mandelbrot();
        mandelbrot.setVisible(true);
    }
}

class Mandelbrot extends JFrame {
    private BufferedImage image;
    private int threadCount = 12;
    private int height = 0;
    private int width = 0;

    Mandelbrot() throws ExecutionException, InterruptedException {
        super("Mandelbrot Set");
        setBounds(100, 100, 800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        height = getHeight();
        width = getWidth();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future> futureThreads = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            int finalI = i;
            futureThreads.add(executorService.submit(() -> parallelDraw(finalI)));
        }

        for(Future item : futureThreads) {
            item.get();
        }
    }

    private void parallelDraw(int threadId) {
        double zy, zx, cX, cY;
        double ZOOM = 150;
        int maxIteration;

        for (int y = threadId; y < height; y += threadCount) {
            for (int x = 0; x < width; x++) {
                zx = zy = 0;
                cX = (x - 400) / ZOOM;
                cY = (y - 300) / ZOOM;
                maxIteration = 570;
                while (zx * zx + zy * zy < 4 && maxIteration > 0) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    maxIteration--;
                }
                image.setRGB(x, y, maxIteration | (maxIteration << 8));
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

}
