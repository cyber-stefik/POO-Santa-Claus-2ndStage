package giftstrategy;

import common.Constants;
import database.Database;

public class AssignGiftsStrategyFactory {
    /**
     *
     * @param database the database that contains all of the information
     * @param strategy the strategy type
     * @return the database, with given gifts
     */
    public static AssignGiftsStrategy createStrategy(final Database database,
                                                     final String strategy) {
        return switch (strategy) {
            case Constants.ID -> new IdAssignStrategyStrategy(database);
            case Constants.NICESCORE -> new NiceScoreStrategy(database);
            case Constants.NICESCORECITY -> new NiceScoreCityStrategy(database);
            default -> null;
        };
    }
}
