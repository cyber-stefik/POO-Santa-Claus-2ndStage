package giftstrategy;

import database.Database;
import entities.Child;
import entities.City;
import entities.Gift;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public class NiceScoreCityStrategy implements AssignGiftsStrategy {
    private Database database;

    /**
     *
     * @param database
     */
    public NiceScoreCityStrategy(final Database database) {
        this.database = database;
    }

    /**
     *
     * @return
     */
    public Database getDatabase() {
        return database;
    }

    /**
     *
     * @param database
     */
    public void setDatabase(final Database database) {
        this.database = database;
    }

    /**
     * Gives gifts to children based on nice score city strategy.
     */
    @Override
    public void getGiftsByStrategy() {
        ArrayList<Child> children = new ArrayList<>(database.getChildren());
        ArrayList<City> cities = new ArrayList<>();
        getCities(children, cities);
        countInhabitants(children, cities);
        calculateAverage(children, cities);
        addGifts(children, cities);
    }

    /**
     * Calculates the average score of a city and sort them by the score.
     * If there are two cities with the same score, they will be sorted.
     * lexicographic
     * @param children
     * @param cities
     */
    private void calculateAverage(final ArrayList<Child> children,
                                  final ArrayList<City> cities) {
        for (City city : cities) {
            double average = 0.0;
            for (Child child : children) {
                if (city.getName().equals(child.getCity())) {
                    average += child.getAverageScore();
                }
            }
            city.setAverage(average / city.getInhabitants());
        }
        cities.sort((o1, o2) -> {
            if (o1.getAverage() == o2.getAverage()) {
                return o2.getName().compareTo(o1.getName());
            } else {
                return Double.compare(o1.getAverage(), o2.getAverage());
            }
        });
        Collections.reverse(cities);

    }

    /**
     * Gives gifts to children based on their preferences
     * @param children list of children
     * @param cities list of cities
     */
    public void addGifts(final ArrayList<Child> children,
                         final ArrayList<City> cities) {
        for (City city : cities) {
            for (Child child : children) {
                // if the child doesn't live in the city, continue to the next
                // one
                if (!city.getName().equals(child.getCity())) {
                    continue;
                }
                Double childAssignedBudget = child.getAssignedBudget();
                for (String preference : child.getGiftsPreferences()) {
                    // array in which I'll add gifts in the same category
                    ArrayList<Gift> preferenceGifts = new ArrayList<>();
                    for (Gift gift : database.getPresents()) {
                        if (gift.getCategory().contains(preference)) {
                            // verify if the budget allows santa to give the
                            // gift and if he has the quantity to do that
                            if (childAssignedBudget >= gift.getPrice()
                                    && gift.getQuantity() > 0) {
                                preferenceGifts.add(gift);
                            }
                        }
                    }
                    sortPreferenceGifts(preferenceGifts);
                    for (Gift gift : preferenceGifts) {
                        int quantity = gift.getQuantity();
                        childAssignedBudget -= gift.getPrice();
                        int ok = 1;
                        // verify if the child already has been given the gift
                        // and if the santa budget allowed santa to
                        // give the gift
                        if (!child.getReceivedGifts().contains(gift)
                            && childAssignedBudget >= 0) {

                            for (int i = 0; i < child.getReceivedGifts().size();
                                                        i++) {
                                // verify if the child already has a gift from a
                                // preferred category
                                if (child.getReceivedGifts().get(i)
                                        .getCategory().contains(preference)) {
                                    ok = 0;
                                    break;
                                }
                            }
                            if (ok == 1) {
                                child.getReceivedGifts().add(gift);
                                gift.setQuantity(quantity - 1);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Sorts by the price, so the first gift will be the cheapest.
     * @param preferenceGifts
     */
    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                    preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }

    /**
     * Counts the number of children in every city.
     * @param children children from the database
     * @param cities all cities
     */
    private void countInhabitants(final ArrayList<Child> children,
                                  final ArrayList<City> cities) {
        for (City city : cities) {
            for (Child child : children) {
                if (city.getName().equals(child.getCity())) {
                    city.setInhabitants(city.getInhabitants() + 1);
                }
            }
        }
    }

    /**
     * It finds every city that a child could live in.
     * @param children children from the database
     * @param cities arraylist which will contain the cities
     */
    private void getCities(final ArrayList<Child> children,
                           final ArrayList<City> cities) {
        // add every city
        for (Child child : children) {
            cities.add(new City(child.getCity(), 0));
        }
        // remove the duplicates
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                if (cities.get(i).getName().equals(cities.get(j).getName())) {
                    cities.remove(cities.get(j));
                }
            }
        }
    }


}
