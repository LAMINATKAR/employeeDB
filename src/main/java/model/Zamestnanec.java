package model;

import java.util.ArrayList;
import java.util.Map;

public abstract class Zamestnanec {
    private int id;
    private String meno;
    private String priezvisko;
    private int rokNarodenia;
    private ArrayList<Spolupraca> spolupracovnici;

    public Zamestnanec(int id, String meno, String priezvisko, int rokNarodenia) {
        this.id               = id;
        this.meno             = meno;
        this.priezvisko       = priezvisko;
        this.rokNarodenia     = rokNarodenia;
        this.spolupracovnici  = new ArrayList<>();
    }

    public abstract void spustiDovednost(java.util.Map<Integer, Zamestnanec> vsetci);
    public abstract String getSkupina();

    public void pridajSpolupraca(int kolegaId, UrovenSpoluprace uroven) {
        for (Spolupraca s : spolupracovnici) {
            if (s.getKolegaId() == kolegaId) {
                s.setUroven(uroven);
                return;
            }
        }
        spolupracovnici.add(new Spolupraca(kolegaId, uroven));
    }

    public void odoberSpolupraca(int kolegaId) {
        spolupracovnici.removeIf(s -> s.getKolegaId() == kolegaId);
    }

    public ArrayList<Spolupraca> getSpolupracovnici() { return spolupracovnici; }

    public String getInfo(Map<Integer, Zamestnanec> vsetci) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Meno: ").append(meno).append(" ").append(priezvisko).append("\n");
        sb.append("Rok narodenia: ").append(rokNarodenia).append("\n");
        sb.append("Skupina: ").append(getSkupina()).append("\n");
        sb.append("Počet spolupracovníkov: ").append(spolupracovnici.size()).append("\n");

        if (!spolupracovnici.isEmpty()) {
            sb.append("Spolupracovníci:\n");
            for (Spolupraca s : spolupracovnici) {
                Zamestnanec k = vsetci != null ? vsetci.get(s.getKolegaId()) : null;
                String meno = k != null ? k.getMeno() + " " + k.getPriezvisko() + " (ID=" + s.getKolegaId() + ")" : "ID=" + s.getKolegaId();
                sb.append("  - ").append(meno).append(" | Úroveň=").append(s.getUroven().toDisplay()).append("\n");
            }
        }

        long dobra     = spolupracovnici.stream().filter(s -> s.getUroven() == UrovenSpoluprace.DOBRA).count();
        long priemerna = spolupracovnici.stream().filter(s -> s.getUroven() == UrovenSpoluprace.PRIEMERNA).count();
        long slaba     = spolupracovnici.stream().filter(s -> s.getUroven() == UrovenSpoluprace.SLABA).count();
        sb.append("Štatistiky: Dobrá=").append(dobra)
          .append(", Priemerná=").append(priemerna)
          .append(", Slabá=").append(slaba).append("\n");

        return sb.toString();
    }

    public int getId()              { return id; }
    public String getMeno()         { return meno; }
    public String getPriezvisko()   { return priezvisko; }
    public int getRokNarodenia()    { return rokNarodenia; }
    public void setId(int id)        { this.id = id; }
    public void setMeno(String m)   { this.meno = m; }
    public void setPriezvisko(String p) { this.priezvisko = p; }
}
