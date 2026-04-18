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
