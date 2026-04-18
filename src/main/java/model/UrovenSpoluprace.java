package model;

public enum UrovenSpoluprace {
    SLABA, PRIEMERNA, DOBRA;

    public String toDisplay() {
        return switch (this) {
            case SLABA     -> "Slabá";
            case PRIEMERNA -> "Priemerná";
            case DOBRA     -> "Dobrá";
        };
    }

}
