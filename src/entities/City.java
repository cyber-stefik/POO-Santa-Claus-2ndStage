package entities;

public final class City {
    private String name;
    private int inhabitants;
    private double average;

    public City(final String name, final int inhabitants) {
        this.name = name;
        this.inhabitants = inhabitants;
        this.average = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(final int inhabitants) {
        this.inhabitants = inhabitants;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(final double average) {
        this.average = average;
    }

}
