import model.*;

import java.util.Scanner;

public class Main {
    private static final Databaza db = new Databaza();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        db.nacitajZoSQL();

        boolean bezi = true;
        while (bezi) {
            vypisMenu();
            String volba = sc.nextLine().trim();
            System.out.println();
            switch (volba) {
                case "1"  -> pridajZamestnanca();
                case "2"  -> pridajSpolupraca();
                case "3"  -> odoberZamestnanca();
                case "4"  -> vyhladajZamestnanca();
                case "5"  -> spustiDovednost();
                case "6"  -> db.vypisAbecedne();
                case "7"  -> db.vypisStatistiky();
                case "8"  -> db.vypisPoctyVSkupinach();
                case "9"  -> ulozDoSuboru();
                case "10" -> nacitajZoSuboru();
                case "0"  -> bezi = false;
                default   -> System.out.println("Neplatná voľba.");
            }
            System.out.println();
        }

        db.ulozDoSQL();
        System.out.println("Program ukončený. Dáta uložené.");
    }

    // ===================== MENU =====================

    private static void vypisMenu() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     DATABÁZA ZAMESTNANCOV            ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1.  Pridať zamestnanca              ║");
        System.out.println("║  2.  Pridať spoluprácu               ║");
        System.out.println("║  3.  Odstrániť zamestnanca           ║");
        System.out.println("║  4.  Vyhľadať zamestnanca podľa ID   ║");
        System.out.println("║  5.  Spustiť dovednosť               ║");
        System.out.println("║  6.  Abecedný výpis podľa skupín     ║");
        System.out.println("║  7.  Globálne štatistiky             ║");
        System.out.println("║  8.  Počty v skupinách               ║");
        System.out.println("║  9.  Uložiť zamestnanca do súboru    ║");
        System.out.println("║  10. Načítať zamestnanca zo súboru   ║");
        System.out.println("║  0.  Ukončiť                         ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Voľba: ");
    }

    // ===================== AKCIE =====================

    private static void pridajZamestnanca() {
        System.out.println("Skupina: 1 = Datový analytik  |  2 = Bezpečnostný špecialist");
        System.out.print("Voľba skupiny: ");
        String skupinaVol = sc.nextLine().trim();

        System.out.print("Meno: ");
        String meno = sc.nextLine().trim();
        System.out.print("Priezvisko: ");
        String priez = sc.nextLine().trim();
        System.out.print("Rok narodenia: ");
        int rok = Integer.parseInt(sc.nextLine().trim());

        int id = db.generateId();
        Zamestnanec z = switch (skupinaVol) {
            case "1" -> new DatovyAnalytik(id, meno, priez, rok);
            case "2" -> new BezpecnostnySpecialista(id, meno, priez, rok);
            default  -> { System.out.println("Neplatná skupina."); yield null; }
        };

        if (z != null) {
            db.pridajZamestnanca(z);
            System.out.println("Zamestnanec pridaný s ID=" + id);
        }
    }

    private static void pridajSpolupraca() {
        System.out.print("ID zamestnanca: ");
        int idA = Integer.parseInt(sc.nextLine().trim());
        System.out.print("ID kolegu: ");
        int idB = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Úroveň (SLABA / PRIEMERNA / DOBRA): ");
        String uStr = sc.nextLine().trim();

        try {
            UrovenSpoluprace u = UrovenSpoluprace.fromString(uStr);
            boolean ok = db.pridajSpolupraca(idA, idB, u);
            System.out.println(ok ? "Spolupráca pridaná." : "Neplatné ID zamestnancov.");
        } catch (IllegalArgumentException e) {
            System.out.println("Neplatná úroveň: " + uStr);
        }
    }

    private static void odoberZamestnanca() {
        System.out.print("ID zamestnanca na odstránenie: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        boolean ok = db.odoberZamestnanca(id);
        System.out.println(ok ? "Zamestnanec odstránený." : "ID nenájdené.");
    }

    private static void vyhladajZamestnanca() {
        System.out.print("ID zamestnanca: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Zamestnanec z = db.najdiPodlaId(id);
        if (z == null) { System.out.println("Zamestnanec nenájdený."); return; }
        System.out.println(z.getInfo());
    }

    private static void spustiDovednost() {
        System.out.print("ID zamestnanca: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Zamestnanec z = db.najdiPodlaId(id);
        if (z == null) { System.out.println("Zamestnanec nenájdený."); return; }
        z.spustiDovednost(db.getMapaVsetkych());
    }

    private static void ulozDoSuboru() {
        System.out.print("ID zamestnanca: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Cesta k súboru (napr. zamestnanec.json): ");
        String cesta = sc.nextLine().trim();
        boolean ok = db.ulozDoSuboru(id, cesta);
        System.out.println(ok ? "Uložené do: " + cesta : "Chyba – ID nenájdené alebo chyba súboru.");
    }

    private static void nacitajZoSuboru() {
        System.out.print("Cesta k súboru: ");
        String cesta = sc.nextLine().trim();
        Zamestnanec z = db.nacitajZoSuboru(cesta);
        System.out.println(z != null
            ? "Načítaný: " + z.getMeno() + " " + z.getPriezvisko() + " (ID=" + z.getId() + ")"
            : "Chyba pri načítaní.");
    }
}
