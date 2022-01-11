package entities;

import java.util.ArrayList;

public final class AnnualChildren {
    private final ArrayList<Children> annualChildren;

    public AnnualChildren(final ArrayList<Children> annualChildren) {
        this.annualChildren = annualChildren;
    }

    public ArrayList<Children> getAnnualChildren() {
        return annualChildren;
    }
}
