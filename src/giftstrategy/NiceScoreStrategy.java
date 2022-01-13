package giftstrategy;

import database.Database;
import entities.Child;
import entities.Gift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class NiceScoreStrategy implements AssignGiftsStrategy {
    private static final int YOUNGADULTAGE = 18;
    private Database database;

    public NiceScoreStrategy(final Database database) {
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
        // remove the young adults
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        // sort the children by average score
        // and by id if 2 children have the same score
        sortChildrenAverage(children);
        // method to give gifts to children
        addGifts(children);
        // sort the children by id at the end, so the database remains intact
        sortChildrenId(children);
    }

    /**
     *
     * @param children list of children
     */
    public void addGifts(final ArrayList<Child> children) {
        for (Child child : children) {
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
                        // and if he has the quantity to do that
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
                    // update the assigned budget
                    childAssignedBudget -= gift.getPrice();
                    // verify if the child already has been given the gift
                    // and if the santa budget allowed santa to give the gift
                    if (!child.getReceivedGifts().contains(gift)
                        && childAssignedBudget >= 0) {
                        // add the cheapest gift
                        child.getReceivedGifts().add(gift);
                        // update the quantity
                        gift.setQuantity(quantity - 1);
                        break;
                    }
                }
            }
        }
    }

    private void sortChildrenId(final ArrayList<Child> children) {
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
        // sort the children by the average first and when 2 average scores
        // are equal, sort those children by id
        Collections.sort(children, new Comparator<Child>() {
            @Override
            public int compare(final Child o1, final Child o2) {
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
