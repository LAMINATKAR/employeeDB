# Databázový systém zaměstnanců

Předpokládejme databázi zaměstnanců technologické firmy. Každý zaměstnanec má své
identifikační číslo, jméno, příjmení a rok narození. Každý zaměstnanec si vede seznam
spolupracovníků, kde u každého eviduje úroveň spolupráce (špatná, průměrná, dobrá).

## Skupiny zaměstnanců

**a) Datoví analytici** – dokážou určit, s kterým spolupracovníkem mají nejvíce společných
spolupracovníků.

**b) Bezpečnostní specialisté** – dokážou vyhodnotit rizikovost spolupráce na základě počtu
spolupracovníků a průměrné kvality spolupráce a vypočítat rizikové skóre (vlastní algoritmus).

Při přijetí do firmy je každý zaměstnanec zařazen do jedné skupiny a nelze jej později přesunout.

## Funkcionalita programu

- **a)** Přidání zaměstnance – uživatel vybere skupinu, zadá jméno, příjmení a rok narození. ID je přiděleno automaticky.
- **b)** Přidání spolupráce – uživatel zadá ID zaměstnance, ID kolegy a úroveň spolupráce.
- **c)** Odebrání zaměstnance – odstranění z databáze včetně všech vazeb.
- **d)** Vyhledání zaměstnance dle ID – výpis základních informací a statistik spolupráce.
- **e)** Spuštění dovednosti zaměstnance dle jeho skupiny.
- **f)** Abecední výpis zaměstnanců podle příjmení ve skupinách.
- **g)** Statistiky – převažující kvalita spolupráce a zaměstnanec s nejvíce vazbami.
- **h)** Výpis počtu zaměstnanců ve skupinách.
- **i)** Uložení zaměstnance do souboru.
- **j)** Načtení zaměstnance ze souboru.
- **k)** Uložení všech dat do SQL databáze při ukončení programu.
- **l)** Načtení všech dat z SQL databáze při spuštění programu.

> Databáze SQL slouží pouze jako záloha dat, program je schopný pracovat i bez použití SQL.

## Požadavky na program

- Využití principů objektově orientovaného programování (OOP).
- Použití alespoň jedné abstraktní třídy nebo rozhraní.
- Použití alespoň jedné dynamické datové struktury.

---

## Implementácia

### Technológie

| Nástroj | Verzia |
|---------|--------|
| Java | 25 |
| Gson | 2.10.1 |
| SQLite JDBC | 3.45.1.0 |
| Build | Maven |

### Štruktúra tried

```
Main
└── Databaza
    ├── Zamestnanec  (abstraktná trieda)
    │   ├── DatovyAnalytik
    │   └── BezpecnostnySpecialista
    ├── Spolupraca
    ├── UrovenSpoluprace  (enum)
    └── ZamestnanecAdapter  (Gson custom adapter)
```

### Dovednosti skupín

**Datový analytik** — nájde spolupracovníka, s ktorým zdieľa najväčší počet spoločných kolegov (priesečník množín).

**Bezpečnostný špecialista** — vypočíta rizikové skóre podľa vzorca:

```
skóre = počet_spolupracovníkov × priemerná_váha_úrovne
```

Váhy úrovní: Dobrá = 1, Priemerná = 2, Slabá = 3. Čím vyššie skóre, tým väčšie riziko.

| Skóre | Hodnotenie |
|-------|-----------|
| ≤ 3 | Nízke riziko |
| ≤ 8 | Stredné riziko |
| ≤ 15 | Vysoké riziko |
| > 15 | Kritické riziko |

### Ukladanie dát

- **SQL (SQLite)** — automaticky pri štarte (načítanie) a pri ukončení (uloženie). Súbor `databaza.db` sa vytvorí vedľa spusteného programu.
- **JSON** — jednotlivý zamestnanec sa uloží/načíta manuálne cez menu. Pri načítaní duplicitného ID sa automaticky pridelí nové.

### Spustenie

```
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

alebo spustiť priamo z IntelliJ IDEA.
