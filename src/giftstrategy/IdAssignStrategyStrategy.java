package giftstrategy;

import database.Database;

public class IdAssignStrategyStrategy implements AssignGiftsStrategy {
    private Database database;

    public IdAssignStrategyStrategy(Database database) {
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
        // id strategy
        return database;
    }
}
