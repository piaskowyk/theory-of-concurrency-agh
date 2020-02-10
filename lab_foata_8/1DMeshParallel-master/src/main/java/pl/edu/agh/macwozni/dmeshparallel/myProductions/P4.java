package pl.edu.agh.macwozni.dmeshparallel.myProductions;

import pl.edu.agh.macwozni.dmeshparallel.mesh.Vertex;
import pl.edu.agh.macwozni.dmeshparallel.production.AbstractProduction;
import pl.edu.agh.macwozni.dmeshparallel.production.PDrawer;

public class P4 extends AbstractProduction<Vertex> {

    public P4(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex t2) {
        System.out.println("p4");
        Vertex t2Prim = new Vertex(t2, null, "T2");
        t2.setRight(t2Prim);
        return t2;
    }
}
