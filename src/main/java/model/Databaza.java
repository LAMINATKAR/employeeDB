package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Databaza {
    private HashMap<Integer, Zamestnanec> zamestnanci = new HashMap<>();
    private int dalsiId = 1;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Zamestnanec.class, new ZamestnanecAdapter())
            .create();

    // ===================== CRUD =====================

    public Zamestnanec pridajZamestnanca(Zamestnanec z) {
        zamestnanci.put(z.getId(), z);
        if (z.getId() >= dalsiId) dalsiId = z.getId() + 1;
        return z;
    }

    public int generateId() { return dalsiId++; }

    public boolean odoberZamestnanca(int id) {
        if (!zamestnanci.containsKey(id)) return false;
        zamestnanci.remove(id);
        // Odstráň všetky väzby na tohto zamestnanca
        for (Zamestnanec z : zamestnanci.values()) {
            z.odoberSpolupraca(id);
        }
        return true;
    }

    public Zamestnanec najdiPodlaId(int id) {
        return zamestnanci.get(id);
    }

    public boolean pridajSpolupraca(int idA, int idB, UrovenSpoluprace uroven) {
        Zamestnanec a = zamestnanci.get(idA);
        Zamestnanec b = zamestnanci.get(idB);
        if (a == null || b == null || idA == idB) return false;
        a.pridajSpolupraca(idB, uroven);
        return true;
    }

    public Collection<Zamestnanec> getVsetci() {
        return zamestnanci.values();
    }

    public Map<Integer, Zamestnanec> getMapaVsetkych() {
        return Collections.unmodifiableMap(zamestnanci);
    }

    // ===================== ABECEDNÝ VÝPIS =====================

    public void vypisAbecedne() {
        List<Zamestnanec> analytici = zamestnanci.values().stream()
                .filter(z -> z instanceof DatovyAnalytik)
                .sorted(Comparator.comparing(Zamestnanec::getPriezvisko))
                .collect(Collectors.toList());

        List<Zamestnanec> specialisti = zamestnanci.values().stream()
                .filter(z -> z instanceof BezpecnostnySpecialista)
                .sorted(Comparator.comparing(Zamestnanec::getPriezvisko))
                .collect(Collectors.toList());

        System.out.println("--- Datoví analytici (" + analytici.size() + ") ---");
        analytici.forEach(z -> System.out.println("  " + z.getPriezvisko() + " " + z.getMeno() + " (ID=" + z.getId() + ")"));

        System.out.println("--- Bezpečnostní špecialisti (" + specialisti.size() + ") ---");
        specialisti.forEach(z -> System.out.println("  " + z.getPriezvisko() + " " + z.getMeno() + " (ID=" + z.getId() + ")"));
    }

    // ===================== ŠTATISTIKY =====================

    public void vypisStatistiky() {
        Collection<Zamestnanec> vsetci = zamestnanci.values();
        if (vsetci.isEmpty()) { System.out.println("Žiadni zamestnanci."); return; }

        long dobra = 0, priemerna = 0, slaba = 0;
        Zamestnanec najviacVazieb = null;
        int maxVazieb = -1;

        for (Zamestnanec z : vsetci) {
            for (Spolupraca s : z.getSpolupracovnici()) {
                switch (s.getUroven()) {
                    case DOBRA     -> dobra++;
                    case PRIEMERNA -> priemerna++;
                    case SLABA     -> slaba++;
                }
            }
            if (z.getSpolupracovnici().size() > maxVazieb) {
                maxVazieb = z.getSpolupracovnici().size();
                najviacVazieb = z;
            }
        }

        System.out.println("=== Globálne štatistiky ===");
        System.out.println("Dobrá spolupráca   : " + dobra);
        System.out.println("Priemerná spolupráca: " + priemerna);
        System.out.println("Slabá spolupráca   : " + slaba);

        long max = Math.max(dobra, Math.max(priemerna, slaba));
        String prevazujuca = (dobra == max) ? "Dobrá" : (priemerna == max) ? "Priemerná" : "Slabá";
        System.out.println("Prevažujúca        : " + prevazujuca);

        if (najviacVazieb != null) {
            System.out.println("Najviac väzieb     : " + najviacVazieb.getMeno()
                + " " + najviacVazieb.getPriezvisko() + " (" + maxVazieb + " väzieb)");
        }
    }

    public void vypisPoctyVSkupinach() {
        long analytici   = zamestnanci.values().stream().filter(z -> z instanceof DatovyAnalytik).count();
        long specialisti = zamestnanci.values().stream().filter(z -> z instanceof BezpecnostnySpecialista).count();
        System.out.println("Datoví analytici        : " + analytici);
        System.out.println("Bezpečnostní špecialisti: " + specialisti);
        System.out.println("Spolu                   : " + zamestnanci.size());
    }

    // ===================== JSON SÚBOR (jednotlivec) =====================

    public boolean ulozDoSuboru(int id, String cestaSuboru) {
        Zamestnanec z = zamestnanci.get(id);
        if (z == null) return false;
        try (Writer w = new FileWriter(cestaSuboru)) {
            GSON.toJson(z, Zamestnanec.class, w);
            return true;
        } catch (IOException e) {
            System.err.println("Chyba pri ukladaní: " + e.getMessage());
            return false;
        }
    }

    public Zamestnanec nacitajZoSuboru(String cestaSuboru) {
        try (Reader r = new FileReader(cestaSuboru)) {
            Zamestnanec z = GSON.fromJson(r, Zamestnanec.class);
            if (z != null) pridajZamestnanca(z);
            return z;
        } catch (IOException e) {
            System.err.println("Chyba pri načítaní: " + e.getMessage());
            return null;
        }
    }

    // ===================== SQLite =====================

    private static final String DB_URL = "jdbc:sqlite:databaza.db";

    public void ulozDoSQL() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            vytvorTabulky(conn);
            conn.setAutoCommit(false);

            try (PreparedStatement psZ = conn.prepareStatement(
                    "INSERT OR REPLACE INTO zamestnanci(id, meno, priezvisko, rok_narodenia, skupina) VALUES(?,?,?,?,?)");
                 PreparedStatement psS = conn.prepareStatement(
                    "INSERT OR REPLACE INTO spoluprace(zamestnanec_id, kolega_id, uroven) VALUES(?,?,?)")) {

                conn.createStatement().execute("DELETE FROM spoluprace");
                conn.createStatement().execute("DELETE FROM zamestnanci");

                for (Zamestnanec z : zamestnanci.values()) {
                    psZ.setInt(1, z.getId());
                    psZ.setString(2, z.getMeno());
                    psZ.setString(3, z.getPriezvisko());
                    psZ.setInt(4, z.getRokNarodenia());
                    psZ.setString(5, z.getSkupina());
                    psZ.addBatch();

                    for (Spolupraca s : z.getSpolupracovnici()) {
                        psS.setInt(1, z.getId());
                        psS.setInt(2, s.getKolegaId());
                        psS.setString(3, s.getUroven().name());
                        psS.addBatch();
                    }
                }
                psZ.executeBatch();
                psS.executeBatch();
                conn.commit();
                System.out.println("Dáta uložené do SQL.");
            }
        } catch (SQLException e) {
            System.err.println("SQL chyba pri ukladaní: " + e.getMessage());
        }
    }

    public void nacitajZoSQL() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            vytvorTabulky(conn);
            zamestnanci.clear();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM zamestnanci")) {
                while (rs.next()) {
                    int id        = rs.getInt("id");
                    String meno   = rs.getString("meno");
                    String priez  = rs.getString("priezvisko");
                    int rok       = rs.getInt("rok_narodenia");
                    String skup   = rs.getString("skupina");

                    Zamestnanec z = skup.equals("Datový analytik")
                            ? new DatovyAnalytik(id, meno, priez, rok)
                            : new BezpecnostnySpecialista(id, meno, priez, rok);
                    zamestnanci.put(id, z);
                    if (id >= dalsiId) dalsiId = id + 1;
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM spoluprace")) {
                while (rs.next()) {
                    int zamId    = rs.getInt("zamestnanec_id");
                    int kolegaId = rs.getInt("kolega_id");
                    UrovenSpoluprace u = UrovenSpoluprace.valueOf(rs.getString("uroven"));
                    Zamestnanec z = zamestnanci.get(zamId);
                    if (z != null) z.pridajSpolupraca(kolegaId, u);
                }
            }
            System.out.println("Dáta načítané z SQL (" + zamestnanci.size() + " zamestnancov).");
        } catch (SQLException e) {
            System.err.println("SQL chyba pri načítaní (pokračujem bez SQL): " + e.getMessage());
        }
    }

    private void vytvorTabulky(Connection conn) throws SQLException {
        conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS zamestnanci (
                id INTEGER PRIMARY KEY,
                meno TEXT, priezvisko TEXT,
                rok_narodenia INTEGER, skupina TEXT
            )""");
        conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS spoluprace (
                zamestnanec_id INTEGER, kolega_id INTEGER,
                uroven TEXT,
                PRIMARY KEY(zamestnanec_id, kolega_id)
            )""");
    }
}
