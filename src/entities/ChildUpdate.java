package entities;

import java.util.ArrayList;

public final class ChildUpdate {
    private final int id;
    private final Double niceScore;
    private final ArrayList<String> giftsPreference;
    private final String elf;

    public ChildUpdate(final int id, final Double niceScore,
                       final ArrayList<String> giftsPreference,
                       final String elf) {
        this.id = id;
        this.niceScore = niceScore;
        this.giftsPreference = giftsPreference;
        this.elf = elf;
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

    public String getElf() {
        return elf;
    }
}
