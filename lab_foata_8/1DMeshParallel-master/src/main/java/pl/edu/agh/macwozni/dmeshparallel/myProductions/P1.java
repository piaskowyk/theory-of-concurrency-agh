package pl.edu.agh.macwozni.dmeshparallel.myProductions;

import pl.edu.agh.macwozni.dmeshparallel.mesh.Vertex;
import pl.edu.agh.macwozni.dmeshparallel.production.AbstractProduction;
import pl.edu.agh.macwozni.dmeshparallel.production.PDrawer;

public class P1 extends AbstractProduction<Vertex> {

    public P1(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex s) {
        System.out.println("p1");
        Vertex t1 = new Vertex(null, null, "T1");
        Vertex t2 = new Vertex(t1, null, "T1");
        t1.setRight(t2);
        return t1;
    }
}
