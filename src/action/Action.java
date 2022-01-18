package action;

import database.Database;

import entities.AnnualChange;
import entities.Child;
import entities.Children;
import entities.Gift;
import fileio.Writer;
import entities.AnnualChildren;
import entities.ChildUpdate;
import giftstrategy.AssignGiftsStrategy;
import giftstrategy.AssignGiftsStrategyFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class Action {
    public static final int YOUNGADULTAGE = 18;
    public static final int BABY = 10;
    public static final int KID = 12;
    public static final int BABYAGE = 5;
    public static final double PERCENT = 100;
    public static final int CHANGE = 30;

    private Action() {

    }

    /**
     * Solves the project, calls methods and the writer.
     * @param database database which contains info about every entity
     * @param writer function to write in the output json
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void solve(final Database database, final Writer writer,
                             final String filePath2) throws IOException {
        ArrayList<Child> children = database.getChildren();
        ArrayList<Gift> gifts = database.getPresents();
        // an ArrayList<ArrayList<Child>> to write in the final json
        ArrayList<Children> annualChildren = new ArrayList<>();
        children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
        roundZero(database);
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
            growUp(children);
            // removing the young adults fom the children list every year
            // of the simulation
            children.removeIf(child -> child.getAge() > YOUNGADULTAGE);
            AnnualChange annualChange = database.getAnnualChanges().get(i - 1);
            if (annualChange.getNewChildren() != null) {
                addNewChildren(children, annualChange);
            }
            if (annualChange.getChildrenUpdates() != null) {
                updateChildren(children, annualChange);
            }
            if (annualChange.getNewGifts() != null) {
                gifts.addAll(annualChange.getNewGifts());
            }
            // set the new santa budget
            database.setSantaBudget(annualChange.getNewSantaBudget());
            Double santaBudget = database.getSantaBudget();
            Double niceScoreSum = calculateNiceScore(children);
            setChildAssignedBudget(database, niceScoreSum);
            receiveGifts(database, i - 1);
            ArrayList<Child> children2 = new ArrayList<>();
            for (Child child : children) {
                // copying every child from the children list in a new list,
                // so the database remains intact for the next rounds
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

    /**
     * It solves the round zero, when every child has the average score his
     * first score.
     * @param database contains information about every entity
     *
     */
    private static void roundZero(final Database database) {
        ArrayList<Child> children = database.getChildren();
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
                // the first score from the score history list +- the bonus
                double averageScore = child.getNiceScoreHistory().get(0);
                averageScore += averageScore * child.getNiceScoreBonus()
                                / PERCENT;
                if (averageScore > BABY) {
                    // reusing constant, BABY = 10
                    averageScore = BABY;
                }
                child.setAverageScore(averageScore);
            }
            // adding to the final sum
            niceScoreSum += child.getAverageScore();
        }
        // setting the budget for every child
        setChildAssignedBudget(database, niceScoreSum);
        receiveGifts(database, -1);
    }

    /**
     * Every child becomes one year older.
     * @param children children list with every child getting one year older
     */
    private static void growUp(final ArrayList<Child> children) {
        for (Child child : children) {
            child.setAge(child.getAge() + 1);
        }
    }

    /**
     * Adds the new children to the database, if there are any.
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
     * Updates children every year.
     * @param children children list from the database
     * @param annualChange the annual change at the round i - 1
     */
    private static void updateChildren(final ArrayList<Child> children,
                                       final AnnualChange annualChange) {
        for (ChildUpdate childUpdate : annualChange
                .getChildrenUpdates()) {
            for (Child child : children) {
                // searching for the child with the given id in the
                // annual change
                if (child.getId() == childUpdate.getId()) {
                    // updates the score if the given score in the annual change
                    // is not null
                    if (childUpdate.getNiceScore() != -1.0) {
                        child.getNiceScoreHistory().add(childUpdate
                                .getNiceScore());
                    }
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
                            child.getGiftsPreferences().add(0,
                                    newPreference);
                        }
                    }
                    child.setElf(childUpdate.getElf());
                }
            }
        }
    }

    /**
     * Calculates and returns the nice score sum of every child.
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
                double averageScore = average / child.getNiceScoreHistory()
                        .size();
                averageScore += averageScore * child.getNiceScoreBonus()
                                / PERCENT;
                if (averageScore > BABY) {
                    // reusing constant, BABY = 10
                    averageScore = BABY;
                }
                child.setAverageScore(averageScore);
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
                double averageScore = pondered / denominator;
                averageScore += averageScore * child.getNiceScoreBonus()
                            / PERCENT;
                if (averageScore > BABY) {
                    // reusing constant, BABY = 10
                    averageScore = BABY;
                }
                child.setAverageScore(averageScore);
            }
            // add to the niceScoreSum every child's average score
            niceScoreSum += child.getAverageScore();
        }
        return niceScoreSum;
    }

    /**
     * Sets the assigned budget for every child.
     * @param database database which contains info about every entity
     * @param niceScoreSum sum of every child's average score
     */
    private static void setChildAssignedBudget(final Database database,
                                               final Double niceScoreSum) {
        ArrayList<Child> children = database.getChildren();
        // calculating the budget unit with the given formula
        Double budgetUnit = database.getSantaBudget() / niceScoreSum;
        for (Child child : children) {
            double assignedBudget = checkElf(budgetUnit, child);
            child.setAssignedBudget(assignedBudget);
        }
    }

    /**
     * Adds or takes to/from the assigned budget.
     * @param budgetUnit the budget unit to calculate the assigned budget
     * @param child child which will have the assigned budget calculated
     *              if he has an elf
     * @return the final budget
     */
    private static double checkElf(final Double budgetUnit,
                                   final Child child) {
        double assignedBudget = child.getAverageScore() * budgetUnit;
        if (child.getElf().equals("black")) {
            assignedBudget -= assignedBudget * CHANGE / PERCENT;
        } else if (child.getElf().equals("pink")) {
            assignedBudget += assignedBudget * CHANGE / PERCENT;
        }
        return assignedBudget;
    }

    /**
     * Gives gifts to children based on a strategy and an elf.
     * Year -1 means the round zero is happening.
     * @param database database which contains info about every entity
     */
    private static void receiveGifts(final Database database, final int
                                     year) {
        ArrayList<Child> children = database.getChildren();
        if (year == -1) {
            AssignGiftsStrategy assignGiftsStrategy = AssignGiftsStrategyFactory
                    .createStrategy(database, "id");
            assignGiftsStrategy.getGiftsByStrategy();
        } else {
            String strategy = database.getAnnualChanges().get(year)
                            .getStrategy();
            AssignGiftsStrategy assignGiftsStrategy = AssignGiftsStrategyFactory
                    .createStrategy(database, strategy);
            assignGiftsStrategy.getGiftsByStrategy();
        }
        // yellow elf
        for (Child child : children) {
            // verify if the child has yellow elf and hasn't received any
            // gifts
            if (child.getElf().equals("yellow")
                    && child.getReceivedGifts().isEmpty()) {
                ArrayList<Gift> preferenceGifts = new ArrayList<>();
                for (Gift gift : database.getPresents()) {
                    // add the gifts of the preferred category
                    // that can still be given to children
                    if (gift.getCategory().contains(child.getGiftsPreferences().get(0))) {
                        if (gift.getQuantity() >= 0) {
                            preferenceGifts.add(gift);
                        }
                    }
                }
                // sort by price so the first one will be the cheapest
                Comparator<Gift> comparator;
                comparator = Comparator.comparing(Gift::getPrice);
                preferenceGifts.sort(comparator);
                for (Gift gift : preferenceGifts) {
                    int quantity = gift.getQuantity();
                    // verify if the gift can still be given
                    if (gift.getQuantity() > 0) {
                        child.getReceivedGifts().add(gift);
                        gift.setQuantity(quantity - 1);
                    }
                    break;
                }
            }
        }
    }

}
