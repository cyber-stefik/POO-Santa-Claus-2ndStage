package entities;

import java.util.ArrayList;

public final class ChildUpdate {
    private final int id;
    private final Double niceScore;
    private final ArrayList<String> giftsPreference;

    public ChildUpdate(final int id, final Double niceScore,
                       final ArrayList<String> giftsPreference) {
        this.id = id;
        this.niceScore = niceScore;
        this.giftsPreference = giftsPreference;
    }

    public int getId() {
        return id;
    }

    public Double getNiceScore() {
        return niceScore;
    }

    public ArrayList<String> getGiftsPreference() {
        return giftsPreference;
    }

    @Override
    public String toString() {
        return "ChildUpdate{"
                + "id=" + id
                + ", niceScore=" + niceScore
                + ", giftsPreference=" + giftsPreference
                + '}';
    }
}
