package giftstrategy;

import database.Database;

public class NiceScoreStrategy implements AssignGiftsStrategy {
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
    public Database getGiftsByStrategy() {
        // niceScore strategy
        return database;
    }
}
