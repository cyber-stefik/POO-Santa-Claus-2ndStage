package fileio;

import common.Constants;
import entities.AnnualChange;
import entities.Child;
import entities.ChildUpdate;
import entities.Gift;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class InputLoader {
    private final String inputPath;

    public InputLoader(final String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    /**
     *
     * @return returns the input json, using different types of variables for
     *          storing the values of each field
     */
    public Input readData() {
        JSONParser jsonParser = new JSONParser();
        String numberOfYears = "0";
        String santaBudget = "0";
        ArrayList<Child> children = new ArrayList<>();
        ArrayList<Gift> santaGiftsList = new ArrayList<>();
        ArrayList<AnnualChange> annualChanges = new ArrayList<>();

        try {
            JSONObject jsonObject = (JSONObject) jsonParser
                    .parse(new FileReader(inputPath));
            numberOfYears = String.valueOf((jsonObject).
                    get(Constants.NUMBEROFYEARS));
            santaBudget = String.valueOf((jsonObject).
                    get(Constants.SANTABUDGET));
            JSONObject initialData = (JSONObject) jsonObject.
                    get(Constants.INITIALDATA);
            JSONArray jsonAnnualChanges = (JSONArray) jsonObject
                    .get(Constants.ANNUALCHANGES);
            JSONArray jsonChildren = (JSONArray)
                    initialData.get(Constants.CHILDREN);
            JSONArray jsonPresents = (JSONArray)
                    initialData.get(Constants.PRESENTS);

            if (jsonChildren != null) {
                for (Object jsonChild : jsonChildren) {
                    createChild(children, (JSONObject) jsonChild);
                }
            } else {
                System.out.println("Nu exista copii");
            }

            if (jsonPresents != null) {
                createGifts(santaGiftsList, jsonPresents);
            } else {
                System.out.println("Nu exista cadouri");
            }
            for (Object annualChange : jsonAnnualChanges) {
                ArrayList<Gift> newGifts = new ArrayList<>();
                ArrayList<Child> newChildren = new ArrayList<>();
                ArrayList<ChildUpdate> childUpdates = new ArrayList<>();
                String newSantaBudget = String.valueOf(((JSONObject)
                        annualChange).get(Constants.NEWSANTABUDGET));
                JSONArray jsonNewGifts = ((JSONArray) ((JSONObject)
                        annualChange).get(Constants.NEWGIFTS));
                JSONArray jsonNewChildren = ((JSONArray) ((JSONObject)
                        annualChange).get(Constants.NEWCHILDREN));
                JSONArray jsonChildrenUpdates = ((JSONArray) ((JSONObject)
                        annualChange).get(Constants.CHILDRENUPDATES));

                createGifts(newGifts, jsonNewGifts);
                for (Object jsonChild : jsonNewChildren) {
                    createChild(newChildren, (JSONObject) jsonChild);
                }
                for (Object jsonChild : jsonChildrenUpdates) {
                    createChildUpdate(childUpdates, (JSONObject) jsonChild);
                }
                annualChanges.add(new AnnualChange(
                        Double.valueOf(newSantaBudget),
                        newGifts, newChildren, childUpdates));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new Input(Integer.parseInt(numberOfYears),
                Double.valueOf(santaBudget), children, santaGiftsList,
                annualChanges);
    }

    private void createChildUpdate(final ArrayList<ChildUpdate> childUpdates,
                           final JSONObject jsonChild) {
        String id = String.valueOf((jsonChild)
                .get(Constants.ID));
        String niceScore = String.valueOf(((JSONObject)
                jsonChild).get(Constants.NICESCORE));
        if (niceScore == "null") {
            niceScore = "-1";
        }

        childUpdates.add(new ChildUpdate(
                Integer.parseInt(id),
                Double.valueOf(niceScore),
                Utils.convertJSONArray((JSONArray)
                        jsonChild.get(Constants.GIFTSPREFERENCES))));
    }

    private void createGifts(final ArrayList<Gift> newGifts,
                             final JSONArray jsonNewGifts) {
        for (Object newGift : jsonNewGifts) {
            String productName = String.valueOf(((JSONObject) newGift)
                    .get(Constants.PRODUCTNAME));
            String price = String.valueOf(((JSONObject) newGift)
                    .get(Constants.PRICE));
            String category = String.valueOf(((JSONObject) newGift)
                    .get(Constants.CATEGORY));
            newGifts.add(new Gift(productName,
                    Double.valueOf(price),
                    category));
        }
    }

    private void createChild(final ArrayList<Child> children,
                             final JSONObject jsonChild) {
        String id = String.valueOf(jsonChild
                .get(Constants.ID));
        String lastName = String.valueOf(jsonChild
                .get(Constants.LASTNAME));
        String firstName = String.valueOf(jsonChild
                .get(Constants.FIRSTNAME));
        String age = String.valueOf(jsonChild
                .get(Constants.AGE));
        String city = String.valueOf(jsonChild
                .get(Constants.CITY));
        String niceScore = String.valueOf(jsonChild
                .get(Constants.NICESCORE));
        // add a child in the database using Builder strategy
        children.add(new Child.ChildBuilder(
                Integer.parseInt(id),
                lastName,
                firstName,
                Integer.parseInt(age),
                city,
                Double.valueOf(niceScore),
                Utils.convertJSONArray((JSONArray)
                        jsonChild.get(Constants.GIFTSPREFERENCES)
                ))
                        .niceScoreHistory(null)
                        .assignedBudget(0.0)
                        .receivedGifts(null)
                .build());
    }
}
