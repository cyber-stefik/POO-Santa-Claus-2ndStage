package database;

import entities.AnnualChange;
import entities.Child;
import entities.Gift;
import fileio.Input;

import java.util.ArrayList;

public final class Database {
    // input data
    private int numberOfYears;
    private Double santaBudget;
    private ArrayList<Child> children = new ArrayList<>();
    private ArrayList<Gift> gifts = new ArrayList<>();
    private ArrayList<AnnualChange> annualChanges = new ArrayList<>();

    // Singleton
    private static final Database INSTANCE = new Database();
    private static int instanceCount = 0;

    private Database() {
        instanceCount++;
    }

    public static Database getDatabase() {
        return INSTANCE;
    }

    public static int getNumberOfInstances() {
        return instanceCount;
    }

    /**
     *
     * @param input input I created in InputLoader from json files
     */
    public void addData(final Input input) {
        this.numberOfYears = input.getNumberOfYears();
        this.santaBudget = input.getSantaBudget();
        this.children = input.getChildren();
        this.gifts = input.getPresents();
        this.annualChanges = input.getAnnualChanges();
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public static void setInstanceCount(final int instanceCount) {
        Database.instanceCount = instanceCount;
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

    public void setNumberOfYears(final int numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    public Double getSantaBudget() {
        return santaBudget;
    }

    public void setSantaBudget(final Double santaBudget) {
        this.santaBudget = santaBudget;
    }

    public void setChildren(final ArrayList<Child> children) {
        this.children = children;
    }

    public void setPresents(final ArrayList<Gift> presents) {
        this.gifts = presents;
    }

    public void setAnnualChanges(final ArrayList<AnnualChange> annualChanges) {
        this.annualChanges = annualChanges;
    }

    @Override
    public String toString() {
        return "Database{"
                + "numberOfYears=" + numberOfYears
                + ", santaBudget=" + santaBudget
                + ", children=" + children
                + ", presents=" + gifts
                + ", annualChanges=" + annualChanges
                + '}';
    }
}
