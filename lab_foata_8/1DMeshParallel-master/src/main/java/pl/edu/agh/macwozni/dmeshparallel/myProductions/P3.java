package pl.edu.agh.macwozni.dmeshparallel.myProductions;

import pl.edu.agh.macwozni.dmeshparallel.mesh.Vertex;
import pl.edu.agh.macwozni.dmeshparallel.production.AbstractProduction;
import pl.edu.agh.macwozni.dmeshparallel.production.PDrawer;

public class P3 extends AbstractProduction<Vertex> {

    public P3(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex t1) {
        System.out.println("p3");
        Vertex t2 = new Vertex(t1.getLeft(), t1, "T2");
        t1.getLeft().setRight(t2);
        t1.setLeft(t2);
        return t2;
    }
}
