package entities;

import java.util.ArrayList;

public final class Children {
    private final ArrayList<Child> children;


    public Children(final ArrayList<Child> children) {
        this.children = children;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }
}
