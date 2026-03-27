package model;

import java.io.Serializable;

public class Spolupraca implements Serializable {
    private int kolegaId;
    private UrovenSpoluprace uroven;

    public Spolupraca(int kolegaId, UrovenSpoluprace uroven) {
        this.kolegaId = kolegaId;
        this.uroven   = uroven;
    }

    public int getKolegaId()           { return kolegaId; }
    public UrovenSpoluprace getUroven() { return uroven; }
    public void setUroven(UrovenSpoluprace u) { this.uroven = u; }

    @Override
    public String toString() {
        return "KolegaId=" + kolegaId + " | Úroveň=" + uroven.toDisplay();
    }
}
