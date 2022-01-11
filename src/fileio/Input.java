package fileio;

import entities.AnnualChange;
import entities.Child;
import entities.Gift;

import java.util.ArrayList;

public final class Input {
    private final int numberOfYears;
    private final Double santaBudget;
    private final ArrayList<Child> children;
    private final ArrayList<Gift> gifts;
    private final ArrayList<AnnualChange> annualChanges;

    public Input() {
        numberOfYears = -1;
        santaBudget = -1.0;
        children = null;
        gifts = null;
        annualChanges = null;
    }

    public Input(final int numberOfYears, final Double santaBudget,
                 final ArrayList<Child> children, final ArrayList<Gift>
                         gifts, final ArrayList<AnnualChange> annualChanges) {

        this.numberOfYears = numberOfYears;
        this.santaBudget = santaBudget;
        this.children = children;
        this.gifts = gifts;
        this.annualChanges = annualChanges;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    public ArrayList<Gift> getPresents() {
        return gifts;
    }

    public ArrayList<AnnualChange> getAnnualChanges() {
        return annualChanges;
    }

    public int getNumberOfYears() {
        return numberOfYears;
    }

    public Double getSantaBudget() {
        return santaBudget;
    }
}
