package pl.edu.agh.macwozni.dmeshparallel;

import pl.edu.agh.macwozni.dmeshparallel.myProductions.P2;
import pl.edu.agh.macwozni.dmeshparallel.myProductions.P6;
import pl.edu.agh.macwozni.dmeshparallel.myProductions.P1;
import pl.edu.agh.macwozni.dmeshparallel.myProductions.P5;
import pl.edu.agh.macwozni.dmeshparallel.myProductions.P3;
import pl.edu.agh.macwozni.dmeshparallel.mesh.Vertex;
import pl.edu.agh.macwozni.dmeshparallel.mesh.GraphDrawer;
import pl.edu.agh.macwozni.dmeshparallel.parallelism.BlockRunner;
import pl.edu.agh.macwozni.dmeshparallel.production.PDrawer;

public class Executor extends Thread {
    
    private final BlockRunner runner;
    
    public Executor(BlockRunner _runner){
        this.runner = _runner;
    }

    @Override
    public void run() {

        PDrawer drawer = new GraphDrawer();
        //axiom
        Vertex s = new Vertex(null, null, "S");

        //p1 
        P1 p1_1 = new P1(s, drawer);
        this.runner.addThread(p1_1);

        //start threads
        this.runner.startAll();

        //p2,p3
        P2 p2_2 = new P2(p1_1.getObj(), drawer);
        P3 p3_2 = new P3(p1_1.getObj().getRight(), drawer);
        this.runner.addThread(p2_2);
        this.runner.addThread(p3_2);

        //start threads
        this.runner.startAll();

        //p5a, p3
        P5 p5A_3 = new P5(p2_2.getObj(), drawer);
        P3 p3_3 = new P3(p3_2.getObj().getRight(), drawer);
        P5 p6_3 = new P5(p3_2.getObj().getRight(), drawer);

        this.runner.addThread(p5A_3);
        this.runner.addThread(p3_3);
        this.runner.addThread(p6_3);

        this.runner.startAll();

        //p5, p6, p5b, p6
        P5 p5_4_1 = new P5(p5A_3.getObj(), drawer);
        P6 p5b_4_3 = new P6(p3_3.getObj().getRight(), drawer);
        P6 p6_4_4 = new P6(p3_3.getObj(), drawer);
        this.runner.addThread(p5_4_1);
        this.runner.addThread(p5b_4_3);
        this.runner.addThread(p6_4_4);

        //start threads
        this.runner.startAll();

        //done
        System.out.println("done");
        drawer.draw(p6_4_4.getObj());

    }
}
