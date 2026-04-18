package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int id, String meno, String priezvisko, int rokNarodenia) {
        super(id, meno, priezvisko, rokNarodenia);
    }

    @Override
    public String getSkupina() { return "Datový analytik"; }

    /**
     * Nájde spolupracovníka, s ktorým má tento analytik
     * najviac spoločných spolupracovníkov (priesečník množín).
     */
    @Override
    public void spustiDovednost(Map<Integer, Zamestnanec> vsetci) {
        System.out.println("=== Dovednosť: Najväčší priesečník spolupracovníkov ===");

        // Množina vlastných spolupracovníkov
        Set<Integer> mojeSpoluprace = new HashSet<>();
        for (Spolupraca s : getSpolupracovnici()) {
            mojeSpoluprace.add(s.getKolegaId());
        }

        if (mojeSpoluprace.isEmpty()) {
            System.out.println("Zamestnanec nemá žiadnych spolupracovníkov.");
            return;
        }

        Zamestnanec najlepsi = null;
        int maxSpolocnych = -1;

        for (Map.Entry<Integer, Zamestnanec> entry : vsetci.entrySet()) {
            Zamestnanec kandidat = entry.getValue();
            if (kandidat.getId() == this.getId()) continue;

            Set<Integer> jehoSpoluprace = new HashSet<>();
            for (Spolupraca s : kandidat.getSpolupracovnici()) {
                jehoSpoluprace.add(s.getKolegaId());
            }

            // Priesečník
            Set<Integer> spolocne = new HashSet<>(mojeSpoluprace);
            spolocne.retainAll(jehoSpoluprace);

            if (spolocne.size() > maxSpolocnych) {
                maxSpolocnych = spolocne.size();
                najlepsi = kandidat;
            }
        }

        if (najlepsi == null || maxSpolocnych == 0) {
            System.out.println("Žiadny spolupracovník nemá spoločných kolegov s týmto zamestnancom.");
        } else {
            System.out.println("Najviac spoločných spolupracovníkov má s:");
            System.out.println("  " + najlepsi.getMeno() + " " + najlepsi.getPriezvisko()
                + " (ID=" + najlepsi.getId() + ")");
            System.out.println("  Počet spoločných: " + maxSpolocnych);
        }
    }
}
