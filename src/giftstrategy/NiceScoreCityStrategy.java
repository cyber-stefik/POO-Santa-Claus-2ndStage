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
     *
     */
    @Override
    public void getGiftsByStrategy() {
        ArrayList<Child> children = new ArrayList<>(database.getChildren());
        ArrayList<City> cities = new ArrayList<>();
        getCities(children, cities);
        countInhabitants(children, cities);
        calculateAverage(children, cities);
        addGifts(children, cities);
//        database.setChildren(children);
    }

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
     *
     * @param children
     * @param cities
     */
    public void addGifts(final ArrayList<Child> children,
                         final ArrayList<City> cities) {
        sortChildrenId(children);
        for (City city : cities) {
            for (Child child : children) {
                if (!city.getName().equals(child.getCity())) {
                    continue;
                }
                // getting the budget of every child
                Double childAssignedBudget = child.getAssignedBudget();
                // getting every preference of every child
                for (String preference : child.getGiftsPreferences()) {
                    // array in which I'll add gifts in the same category
                    ArrayList<Gift> preferenceGifts = new ArrayList<>();
                    for (Gift gift : database.getPresents()) {
                        // verify if the gift category is in the preferred category
                        // of the child
                        if (gift.getCategory().contains(preference)) {
                            // verify if the budget allows santa to give the gift
                            if (childAssignedBudget >= gift.getPrice()
                                    && gift.getQuantity() > 0) {
                                preferenceGifts.add(gift);
                            }
                        }
                    }
                    // sort the preferenceGifts by price, so the first one will be
                    // the cheapest
                    sortPreferenceGifts(preferenceGifts);
                    for (Gift gift : preferenceGifts) {
                        int quantity = gift.getQuantity();
                        childAssignedBudget -= gift.getPrice();
                        int ok = 1;
                        if (!child.getReceivedGifts().contains(gift)
                            && childAssignedBudget >= 0) {
                            for (int i = 0; i < child.getReceivedGifts().size();
                                                        i++) {
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
                        // add the cheapest gift
                        // update the assigned budget
                    }
                }
            }
        }
    }

    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                    preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }

    private void sortChildrenId(final ArrayList<Child> children) {
        Comparator<Child> comparator = Comparator.comparing(Child::getId);
        children.sort(comparator);
    }

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

    private void getCities(final ArrayList<Child> children,
                           final ArrayList<City> cities) {
        for (Child child : children) {
            cities.add(new City(child.getCity(), 0));
        }
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                if (cities.get(i).getName().equals(cities.get(j).getName())) {
                    cities.remove(cities.get(j));
                }
            }
        }
    }


}
