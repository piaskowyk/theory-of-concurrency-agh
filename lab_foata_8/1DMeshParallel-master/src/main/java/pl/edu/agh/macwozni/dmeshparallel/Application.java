package pl.edu.agh.macwozni.dmeshparallel;

import pl.edu.agh.macwozni.dmeshparallel.parallelism.ConcurentBlockRunner;

class Application {

    public static void main(String args[]) {

        Executor e = new Executor(new ConcurentBlockRunner());
        e.start();
    }
}
