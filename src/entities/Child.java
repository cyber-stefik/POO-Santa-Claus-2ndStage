package entities;

import java.util.ArrayList;

public final class Child {
    private final int id;
    private final String lastName;
    private final String firstName;
    private final String city;
    private int age;
    private ArrayList<String> giftsPreferences;
    private Double averageScore;
    private ArrayList<Double> niceScoreHistory;
    private Double assignedBudget;
    private ArrayList<Gift> receivedGifts;

    // Builder pattern
    private Child(final ChildBuilder childBuilder) {
        this.id = childBuilder.id;
        this.lastName = childBuilder.lastName;
        this.firstName = childBuilder.firstName;
        this.city = childBuilder.city;
        this.age = childBuilder.age;
        this.giftsPreferences = childBuilder.giftsPreferences;
        this.averageScore = childBuilder.averageScore;
        this.niceScoreHistory = childBuilder.niceScoreHistory;
        this.assignedBudget = childBuilder.assignedBudget;
        this.receivedGifts = childBuilder.receivedGifts;
    }

    public Child(final Child child) {
        this.id = child.getId();
        this.lastName = child.getLastName();
        this.firstName = child.getFirstName();
        this.age = child.getAge();
        this.city = child.getCity();
        this.averageScore = child.getAverageScore();
        this.giftsPreferences = new ArrayList<>(child.getGiftsPreferences());
        niceScoreHistory = new ArrayList<>(child.getNiceScoreHistory());
        assignedBudget = child.getAssignedBudget();
        receivedGifts = new ArrayList<>(child.getReceivedGifts());
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public ArrayList<String> getGiftsPreferences() {
        return giftsPreferences;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setGiftsPreferences(final ArrayList<String> giftsPreferences) {
        this.giftsPreferences = giftsPreferences;
    }

    public ArrayList<Double> getNiceScoreHistory() {
        return niceScoreHistory;
    }

    public void setNiceScoreHistory(final ArrayList<Double> niceScoreHistory) {
        this.niceScoreHistory = niceScoreHistory;
    }

    public Double getAssignedBudget() {
        return assignedBudget;
    }

    public void setAssignedBudget(final Double assignedBudget) {
        this.assignedBudget = assignedBudget;
    }

    public ArrayList<Gift> getReceivedGifts() {
        return receivedGifts;
    }

    public void setReceivedGifts(final ArrayList<Gift> receivedGifts) {
        this.receivedGifts = receivedGifts;
    }

    public void setAverageScore(final Double averageScore) {
        this.averageScore = averageScore;
    }

    // Builder pattern
    public static class ChildBuilder {
        private final int id;
        private final String lastName;
        private final String firstName;
        private final String city;
        private int age;
        private ArrayList<String> giftsPreferences;
        private Double averageScore;
        private ArrayList<Double> niceScoreHistory;
        private Double assignedBudget;
        private ArrayList<Gift> receivedGifts;

        public ChildBuilder(final int id, final String lastName, final String firstName,
                     final int age, final String city, final Double averageScore,
                     final ArrayList<String> giftsPreference) {
            this.id = id;
            this.lastName = lastName;
            this.firstName = firstName;
            this.age = age;
            this.city = city;
            this.averageScore = averageScore;
            this.giftsPreferences = giftsPreference;
        }

        /**
         *
         * @param niceScoreHistory1 create a new ArrayList of scores
         * @return
         */
        public final ChildBuilder niceScoreHistory(final ArrayList<Double>
                                                     niceScoreHistory1) {
            this.niceScoreHistory = new ArrayList<>();
            this.niceScoreHistory.add(this.averageScore);
            return this;
        }

        /**
         *
         * @param assignedBudget1 the assigned budget, if there will be one in
         *                       the input jsons
         * @return
         */
        public final ChildBuilder assignedBudget(final Double assignedBudget1) {
            this.assignedBudget = assignedBudget1;
            return this;
        }

        /**
         *
         * @param receivedGifts1 create a new ArrayList of gifts
         * @return
         */
        public final ChildBuilder receivedGifts(final ArrayList<Gift>
                                                        receivedGifts1) {
            this.receivedGifts = new ArrayList<>();
            return this;
        }

        /**
         *
         * @return a child object, with the mandatory and optional fields
         */
        public Child build() {
            return new Child(this);
        }
    }

}
