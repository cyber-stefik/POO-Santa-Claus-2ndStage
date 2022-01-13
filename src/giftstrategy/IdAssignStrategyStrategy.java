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
     *
     */
    @Override
    public void getGiftsByStrategy() {
        ArrayList<Child> children = database.getChildren();
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        addGifts(children);
    }

    /**
     *
     * @param children
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

    /**
     *
     * @param preferenceGifts
     */
    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                    preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }
}
