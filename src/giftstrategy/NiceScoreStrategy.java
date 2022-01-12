package giftstrategy;

import database.Database;
import entities.Child;
import entities.City;
import entities.Gift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class NiceScoreStrategy implements AssignGiftsStrategy {
    private static final int YOUNGADULTAGE = 18;
    private Database database;

    public NiceScoreStrategy(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public void getGiftsByStrategy() {
        ArrayList<Child> children = new ArrayList<>(database.getChildren());
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        sortChildrenAverage(children);
        System.out.println(database.getSantaBudget());
        addGifts(children);
        sortChildrenId(children);
        database.setChildren(children);
    }

    public void addGifts(ArrayList<Child> children) {
        for (Child child : children) {
            System.out.println(child);
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
                    System.out.println(gift);
                    int quantity = gift.getQuantity();
                    childAssignedBudget -= gift.getPrice();
                    if (!child.getReceivedGifts().contains(gift)
                        && childAssignedBudget >= 0) {
                        child.getReceivedGifts().add(gift);
                        gift.setQuantity(quantity - 1);
                        break;
                    }
                    // add the cheapest gift
                    // update the assigned budget
                }
            }
        }
    }

    private void sortChildrenId(ArrayList<Child> children) {
        Comparator<Child> comparator = Comparator.comparing(Child::getId);
        children.sort(comparator);
    }

    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                    preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }

    private static void sortChildrenAverage(final ArrayList<Child> children) {
        Collections.sort(children, new Comparator<Child>() {
            @Override
            public int compare(Child o1, Child o2) {
                if (Objects.equals(o1.getAverageScore(), o2.getAverageScore())) {
                    return Integer.compare(o2.getId(), o1.getId());
                } else {
                    return Double.compare(o1.getAverageScore(),
                            o2.getAverageScore());
                }
            }
        });
        Collections.reverse(children);
    }
}
