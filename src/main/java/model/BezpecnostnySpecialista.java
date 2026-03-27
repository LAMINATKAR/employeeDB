package model;

import java.util.List;
import java.util.Map;

public class BezpecnostnySpecialista extends Zamestnanec {

    public BezpecnostnySpecialista(int id, String meno, String priezvisko, int rokNarodenia) {
        super(id, meno, priezvisko, rokNarodenia);
    }

    @Override
    public String getSkupina() { return "Bezpečnostný špecialist"; }

    /**
     * Rizikové skóre = počet_spolupracovníkov × priemerná_váha_úrovne
     * Váhy: DOBRA=1, PRIEMERNA=2, SLABA=3
     * Čím vyššie skóre, tým väčšie riziko (veľa slabých spoluprác).
     */
    public double vypocitajRizikoveSkore() {
        List<Spolupraca> zoznam = getSpolupracovnici();
        if (zoznam.isEmpty()) return 0.0;

        double sumaVah = 0;
        for (Spolupraca s : zoznam) {
            sumaVah += switch (s.getUroven()) {
                case DOBRA     -> 1;
                case PRIEMERNA -> 2;
                case SLABA     -> 3;
            };
        }

        double priemer = sumaVah / zoznam.size();
        return Math.round(zoznam.size() * priemer * 100.0) / 100.0;
    }

    public String interpretaciaRizika(double skore) {
        if (skore <= 3)  return "Nízke riziko";
        if (skore <= 8)  return "Stredné riziko";
        if (skore <= 15) return "Vysoké riziko";
        return "Kritické riziko";
    }

    @Override
    public void spustiDovednost(Map<Integer, Zamestnanec> vsetci) {
        System.out.println("=== Dovednosť: Vyhodnotenie rizika spolupráce ===");
        double skore = vypocitajRizikoveSkore();
        System.out.println("Počet spolupracovníkov : " + getSpolupracovnici().size());
        System.out.printf ("Rizikové skóre         : %.2f%n", skore);
        System.out.println("Hodnotenie             : " + interpretaciaRizika(skore));
        System.out.println();
        System.out.println("Vzorec: počet_spoluprác × priemerná_váha");
        System.out.println("(Váhy: Dobrá=1, Priemerná=2, Slabá=3)");
    }
}
