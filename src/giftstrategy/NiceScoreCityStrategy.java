package giftstrategy;

import database.Database;

public class NiceScoreCityStrategy implements AssignGiftsStrategy {
    private Database database;

    public NiceScoreCityStrategy(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public Database getGiftsByStrategy() {
        // niceScoreCity strategy
        return database;
    }
}
