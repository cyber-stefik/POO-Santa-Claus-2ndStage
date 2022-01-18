package giftstrategy;

import database.Database;
import entities.Child;
import entities.Gift;

import java.util.ArrayList;
import java.util.Comparator;

public class IdAssignStrategyStrategy implements AssignGiftsStrategy {
    private static final int YOUNGADULTAGE = 18;
    private Database database;

    public IdAssignStrategyStrategy(final Database database) {
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
     * Gives gifts to children based on id strategy.
     */
    @Override
    public void getGiftsByStrategy() {
        ArrayList<Child> children = database.getChildren();
        // remove the young adults
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        addGifts(children);
    }

    /**
     * Gives gifts to children based on their preferences.
     * @param children list of children
     */
    public void addGifts(final ArrayList<Child> children) {
        for (Child child : children) {
            Double childAssignedBudget = child.getAssignedBudget();
            for (String preference : child.getGiftsPreferences()) {
                // array in which I'll add gifts in the same category
                ArrayList<Gift> preferenceGifts = new ArrayList<>();
                for (Gift gift : database.getPresents()) {
                    if (gift.getCategory().contains(preference)) {
                        // verify if the budget allows santa to give the gift
                        // and if he has the quantity to do that
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
                    // and if the santa budget allowed santa to give the gift
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

    /**
     * Sorts by the price, so the first gift will be the cheapest.
     * @param preferenceGifts the gifts in a category
     */
    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                    preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }
}
