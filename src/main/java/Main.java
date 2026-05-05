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
        System.out.println("║  0.  Ukončiť a uložiť                ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Voľba: ");
    }

    private static int nacitajInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Zadaj celé číslo.");
            }
        }
    }

    private static String nacitajString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String vstup = sc.nextLine().trim();
            if (!vstup.isEmpty()) return vstup;
            System.out.println("Pole nesmie byť prázdne.");
        }
    }

    private static int nacitajRok(String prompt) {
        while (true) {
            int rok = nacitajInt(prompt);
            if (rok > 0) return rok;
            System.out.println("Rok musí byť kladné číslo.");
        }
    }

    private static void pridajZamestnanca() {
        System.out.println("Skupiny: ");
        System.out.println("| 1 = Datový analytik");
        System.out.println("| 2 = Bezpečnostný špecialista");
        System.out.println("Voľba: ");
        String skupinaVolba = sc.nextLine().trim();

        if (!skupinaVolba.equals("1") && !skupinaVolba.equals("2")) {
            System.out.println("Neplatná skupina.");
            return;
        }

        String meno = nacitajString("Meno: ");
        String priezvisko = nacitajString("Priezvisko: ");
        int rok = nacitajRok("Rok narodenia: ");

        int id = db.generateId();
        Zamestnanec z = skupinaVolba.equals("1")
                ? new DatovyAnalytik(id, meno, priezvisko, rok)
                : new BezpecnostnySpecialista(id, meno, priezvisko, rok);

        db.pridajZamestnanca(z);
        System.out.println("Zamestnanec pridaný s ID=" + id);
    }

    private static void pridajSpolupraca() {
        int idA = nacitajInt("ID zamestnanca: ");
        int idB = nacitajInt("ID kolegu: ");
        System.out.print("Úroveň (1=Dobrá / 2=Priemerná / 3=Slabá): ");
        String urovenVolba = sc.nextLine().trim();

        UrovenSpoluprace u = switch (urovenVolba) {
            case "1" -> UrovenSpoluprace.DOBRA;
            case "2" -> UrovenSpoluprace.PRIEMERNA;
            case "3" -> UrovenSpoluprace.SLABA;
            default  -> null;
        };

        if (u == null) {
            System.out.println("Neplatná úroveň");
            return;
        }

        boolean ok = db.pridajSpolupraca(idA, idB, u);
        System.out.println(ok ? "Spolupráca pridaná." : "Neplatné ID zamestnancov.");
    }

    private static void odoberZamestnanca() {
        int id = nacitajInt("ID zamestnanca na odstránenie: ");
        Zamestnanec z = db.najdiPodlaId(id);
        if (z == null) { System.out.println("ID nenájdené."); return; }
        db.odoberZamestnanca(id);
        System.out.println("Zamestnanec " + z.getMeno() + " " + z.getPriezvisko() + " odstránený.");
    }

    private static void vyhladajZamestnanca() {
        int id = nacitajInt("ID zamestnanca: ");
        Zamestnanec z = db.najdiPodlaId(id);
        if (z == null) { System.out.println("Zamestnanec nenájdený."); return; }
        System.out.println(z.getInfo(db.getMapaVsetkych()));
    }

    private static void spustiDovednost() {
        int id = nacitajInt("ID zamestnanca: ");
        Zamestnanec z = db.najdiPodlaId(id);
        if (z == null) { System.out.println("Zamestnanec nenájdený."); return; }
        System.out.println("Zamestnanec: " + z.getMeno() + " " + z.getPriezvisko() + " (" + z.getSkupina() + ")");
        z.spustiDovednost(db.getMapaVsetkych());
    }

    private static void ulozDoSuboru() {
        int id = nacitajInt("ID zamestnanca: ");
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
