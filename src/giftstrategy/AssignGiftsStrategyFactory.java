package giftstrategy;

import common.Constants;
import database.Database;

public class AssignGiftsStrategyFactory {
    public static AssignGiftsStrategy createStrategy(Database database,
                                                     String strategy) {
        return switch (strategy) {
            case Constants.ID -> new IdAssignStrategyStrategy(database);
            case Constants.NICESCORE -> new NiceScoreStrategy(database);
            case Constants.NICESCORECITY -> new NiceScoreCityStrategy(database);
            default -> null;
        };
    }
}
