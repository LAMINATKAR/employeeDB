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

    public static UrovenSpoluprace fromString(String s) {
        return switch (s.toUpperCase()) {
            case "SLABA"     -> SLABA;
            case "PRIEMERNA" -> PRIEMERNA;
            case "DOBRA"     -> DOBRA;
            default -> throw new IllegalArgumentException("Neznáma úroveň: " + s);
        };
    }
}
