package lab1.zad2;

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

    synchronized void increment() {
        this.state++;
    }

    synchronized void decrement() {
        this.state--;
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