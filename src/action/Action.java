package action;

import database.Database;

import entities.AnnualChange;
import entities.Child;
import entities.Children;
import entities.Gift;
import fileio.Writer;
import entities.AnnualChildren;
import entities.ChildUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class Action {
    public static final int YOUNGADULTAGE = 18;
    public static final int BABY = 10;
    public static final int KID = 12;
    public static final int BABYAGE = 5;

    private Action() {

    }

    /**
     * @param database database which contains info about every entity
     * @param writer function to write in the output json
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void solve(final Database database, final Writer writer,
                             final String filePath2) throws IOException {
        // getting the children list from database
        ArrayList<Child> children = new ArrayList<>(database.getChildren());
        // getting the gifts list from database
        ArrayList<Gift> gifts = new ArrayList<>(database.getPresents());
        // an ArrayList<ArrayList<Child>> to write in the final json
        ArrayList<Children> annualChildren = new ArrayList<>();
        // removing the young adults fom the children list
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        // method for solving the round zero
        roundZero(database);
        // a copy of the children list
        ArrayList<Child> children1 = new ArrayList<>();
        for (Child child : children) {
            // copying every child from the children list in a new list, so the
            // database remains intact for the next rounds
            children1.add(new Child(child));
            child.getReceivedGifts().removeAll(child.getReceivedGifts());
        }
        // add the copy list into the final array
        annualChildren.add(new Children(children1));
        for (int i = 1; i <= database.getNumberOfYears(); i++) {
            // every child becomes 1 year older
            growUp(children);
            // removing the young adults fom the children list
            children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
            // getting the annual changes
            AnnualChange annualChange = database.getAnnualChanges().get(i - 1);
            // add new children if they exist in the annual change
            if (annualChange.getNewChildren() != null) {
                addNewChildren(children, annualChange);
            }
            // update the children if there are child updates
            if (annualChange.getChildrenUpdates() != null) {
                updateChildren(children, annualChange);
            }
            // add new gifts if there are new gifts
            if (annualChange.getNewGifts() != null) {
                gifts.addAll(annualChange.getNewGifts());
            }
            // set the new santa budget
            database.setSantaBudget(annualChange.getNewSantaBudget());
            Double santaBudget = database.getSantaBudget();
            // niceScoreSum is the sum of every child's average score
            Double niceScoreSum = calculateNiceScore(children);
            // calculating the assigned budget for every child
            Double budgetUnit = santaBudget / niceScoreSum;
            for (Child child : children) {
                child.setAssignedBudget(budgetUnit * child.getAverageScore());
            }
            // method to give gifts to children
            receiveGifts(database, children);
            ArrayList<Child> children2 = new ArrayList<>();
            for (Child child : children) {
                // copying every child from the children list in a new list, so the
                // database remains intact for the next rounds
                children2.add(new Child(child));
                child.getReceivedGifts().removeAll(child.getReceivedGifts());
            }
            // add the copy list into the final array
            annualChildren.add(new Children(children2));
        }
        // write the results in the final json
        AnnualChildren annualChildren1 = new AnnualChildren(annualChildren);
        writer.writeFile(annualChildren1, filePath2);
    }

    private static void roundZero(final Database database) throws IOException {
        // getting the children from the database
        ArrayList<Child> children = database.getChildren();
        // calculating the sum of every child's average score
        Double niceScoreSum = 0.0;
        // removing the young adults fom the children list
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        for (Child child : children) {
            if (child.getAge() < BABYAGE) {
                // if the given child is under 5 years old, the averageScore
                // will be 10
                child.setAverageScore((double) BABY);
            } else {
                // for the rest of the children, the average score will be
                // the first score from the score history list
                child.setAverageScore(child.getNiceScoreHistory().get(0));
            }
            // adding to the sum
            niceScoreSum += child.getAverageScore();
        }
        // setting the budget for every child
        setChildAssignedBudget(database, niceScoreSum);
        // method to give every child gifts
        receiveGifts(database, children);
    }

    /**
     *
     * @param children children list with every child getting one year older
     */
    private static void growUp(final ArrayList<Child> children) {
        for (Child child : children) {
            child.setAge(child.getAge() + 1);
        }
    }

    /**
     *
     * @param children children list from the database
     * @param annualChange the annual change at the round i - 1
     */
    private static void addNewChildren(final ArrayList<Child> children,
                                       final AnnualChange annualChange) {
        for (Child child : annualChange.getNewChildren()) {
            if (child.getAge() <= YOUNGADULTAGE) {
                children.add(new Child(child));
            }
        }
    }

    /**
     *
     * @param children children list from the database
     * @param annualChange the annual change at the round i - 1
     */
    private static void updateChildren(final ArrayList<Child> children,
                                       final AnnualChange annualChange) {
        // getting every child update
        for (ChildUpdate childUpdate : annualChange
                .getChildrenUpdates()) {
            // getting every child
            for (Child child : children) {
                // searching for the child with the given id in the
                // annual change
                if (child.getId() == childUpdate.getId()) {
                    // if the score is null in the update, i used -1.0
                    // and verify now if the annual change score is not null
                    if (childUpdate.getNiceScore() != -1.0) {
                        child.getNiceScoreHistory().add(childUpdate
                                .getNiceScore());
                    }
                    // verify if the annual change gift preferences exist
                    if (childUpdate.getGiftsPreference() != null) {
                        for (int j = childUpdate.getGiftsPreference()
                                .size() - 1; j >= 0; j--) {
                            /*
                             if the annual gift preferences contains a
                             gift preference that a child already has
                             I have to take it out from the child's gift
                             preferences list and add it at the beginning
                            */
                            String newPreference =
                                    childUpdate.getGiftsPreference().get(j);
                            child.getGiftsPreferences()
                                    .remove(newPreference);
                            // add the new gift preference
                            child.getGiftsPreferences().add(0,
                                    newPreference);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param children children list from the database
     * @return the nice score sum of every child's average score
     */
    private static Double calculateNiceScore(final ArrayList<Child> children) {
        Double niceScoreSum = 0.0;
        for (Child child : children) {
            if (child.getAge() < BABYAGE) { // set the average score ten
                                            // for babies
                child.setAverageScore((double) BABY);
            } else if (child.getAge() < KID) {
                // calculating the average of score history for kids
                Double average = 0.0;
                for (Double score : child.getNiceScoreHistory()) {
                    average += score;
                }
                child.setAverageScore(average / child.getNiceScoreHistory()
                        .size());
            } else if (child.getAge() <= YOUNGADULTAGE) {
                // calculating the pondered of score history for teens
                double pondered = 0.0;
                int count = 0;
                for (Double score : child.getNiceScoreHistory()) {
                    count++;
                    pondered += score * count;
                }
                double denominator = 0.0;
                for (int j = 1; j <= count; j++) {
                    denominator += j;
                }
                child.setAverageScore(pondered / denominator);
            }
            // add to the niceScoreSum every child's average score
            niceScoreSum += child.getAverageScore();
        }
        return niceScoreSum;
    }

    /**
     *
     * @param database database which contains info about every entity
     * @param niceScoreSum sum of every child's average score
     */
    private static void setChildAssignedBudget(final Database database,
                                               final Double niceScoreSum) {
        // children list from the database
        ArrayList<Child> children = database.getChildren();
        // calculating the budget unit with the given formula
        Double budgetUnit = database.getSantaBudget() / niceScoreSum;
        for (Child child : children) {
            // assign the budget for every child with the given formula
            child.setAssignedBudget(child.getAverageScore() * budgetUnit);
        }
    }

    /**
     *
     * @param database database which contains info about every entity
     * @param children list with every child that will receive gifts
     */
    private static void receiveGifts(final Database database,
                                    final ArrayList<Child> children) {
        // getting every child
        for (Child child : children) {
            // getting the budget of every child
            Double childAssignedBudget = child.getAssignedBudget();
            // getting every preference of every child
            for (String preference : child.getGiftsPreferences()) {
                // array in which I'll add gifts in the same category
                ArrayList<Gift> preferenceGifts = new ArrayList<>();
                for (Gift gift : database.getPresents()) {
                    // verify if the gift category is in the prefered category
                    // of the child
                    if (gift.getCategory().contains(preference)) {
                        // verify if the budget allows santa to give the gift
                        if (childAssignedBudget - gift.getPrice() > 0) {
                            preferenceGifts.add(gift);
                        }
                    }
                }
                // sort the preferenceGifts by price, so the first one will be
                // the cheapest
                sortPreferenceGifts(preferenceGifts);
                for (Gift gift : preferenceGifts) {
                    // add the cheapest gift
                    child.getReceivedGifts().add(gift);
                    // update the assigned budget
                    childAssignedBudget -= gift.getPrice();
                    break;
                }
            }
        }
    }

    /**
     *
     * @param preferenceGifts arraylist of gifts in the same category which
     *                        will be sorted by price
     */
    private static void sortPreferenceGifts(final ArrayList<Gift>
                                                     preferenceGifts) {
        Comparator<Gift> comparator;
        comparator = Comparator.comparing(Gift::getPrice);
        preferenceGifts.sort(comparator);
    }

}
