package pl.edu.agh.macwozni.dmeshparallel.mesh;

import pl.edu.agh.macwozni.dmeshparallel.production.PDrawer;

public class GraphDrawer implements PDrawer<Vertex> {

    @Override
    public void draw(Vertex v) {
        //go left
        while (v.mLeft != null) {
            v = v.mLeft;
        }
        //plot
        while (v.mRight != null) {
            System.out.print(v.mLabel + "--");
            v = v.mRight;
        }
        System.out.println(v.mLabel);
    }
}
