package lab.zad1;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        Counter counter = new Counter();

        ActionIncrement actionIncrement = new ActionIncrement(counter);
        ActionDecrement actionDecrement = new ActionDecrement(counter);

        actionIncrement.start();
        actionDecrement.start();

        actionIncrement.join();
        actionDecrement.join();

        System.out.println(counter.state);
    }
}

class Counter {
    int state;
    private Semaphore semaphore = new Semaphore();

    void increment() {
        semaphore.get();
        this.state++;
        semaphore.release();
    }

    void decrement() {
        semaphore.get();
        this.state--;
        semaphore.release();
    }
}

class Semaphore{

    enum SemaphoreState{
        FREE, BUSY
    }

    private SemaphoreState state = SemaphoreState.FREE;

    synchronized void get() {
        while(state.equals(SemaphoreState.BUSY)){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        state = SemaphoreState.BUSY;
    }

    synchronized void release() {
        state = SemaphoreState.FREE;
        notifyAll();
    }
}

class ActionIncrement extends Thread {

    private Counter counter;
    ActionIncrement(Counter counter){
        this.counter = counter;
    }

    @Override
    public void run() {
        int i = 0;
        while(i < 1000000) {
            i++;
            counter.increment();
        }
    }
}

class ActionDecrement extends Thread {

    private Counter counter;

    ActionDecrement(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 1000000) {
            i++;
            counter.decrement();
        }
    }
}