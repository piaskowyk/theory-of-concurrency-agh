package pl.edu.agh.macwozni.dmeshparallel.mesh;

public class Vertex {

    //label
    String mLabel;
    //links to adjacent elements
    Vertex mLeft;
    Vertex mRight;

    //methods for adding links
    public Vertex(Vertex _left, Vertex _right, String _lab) {
        this.mLeft = _left;
        this.mRight = _right;
        this.mLabel = _lab;
    }
    //empty constructor

    public Vertex() {
    }

    public void setLeft(Vertex _left) {
        this.mLeft = _left;
    }

    public void setRight(Vertex _right) {
        this.mRight = _right;
    }

    public void setLabel(String _lab) {
        this.mLabel = _lab;
    }

    public Vertex getLeft() {
        return this.mLeft;
    }

    public Vertex getRight() {
        return this.mRight;
    }

    public String getLabel() {
        return this.mLabel;
    }
}
