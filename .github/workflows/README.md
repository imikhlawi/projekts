# GitHub Actions – Anpassung für Projekte

Diese Workflows laufen nur, wenn Dateien in den jeweiligen Projektordnern geändert werden (**Path-Filter**). Du kannst Branches, Jobs und Schritte anpassen.

---

## Übersicht der Workflows

| Datei | Projekte | Trigger (Paths) |
|-------|----------|------------------|
| **ci-kotlin.yml** | SchimmenSpiel, CableCar | `SchimmenSpiel/**`, `CableCar/**` |
| **ci-java.yml** | Auftragsverwaltung | `Auftragsverwaltung/**` |
| **ci-python.yml** | BA-Handschrifterkennung, KI-Handels-Framework | `BA-Handschrifterkennung/**`, `KI-Handels-Framework/**` |

**Nicht abgedeckt:** PC-Konfigurator (Eclipse/Xtext), Analyse_Indikatoren (Pine Script).

---

## Was du anpassen solltest

### 1. Branch-Namen

Standard: `main` und `master`. Wenn dein Standard-Branch anders heißt:

```yaml
on:
  push:
    branches: [main, dein-branch]
  pull_request:
    branches: [main, dein-branch]
```

### 2. CableCar: Secret für SoPra-Maven

CableCar braucht Abhängigkeiten vom SoPra-GitLab. Dafür:

1. **GitHub Repo** → **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret:** Name `SOPRA_GITLAB_TOKEN`, Wert = dein GitLab Private Token
3. Der Workflow setzt `SOPRA_GITLAB_TOKEN` als Umgebungsvariable; ohne Secret schlägt der CableCar-Build ggf. fehl.

**Alternative:** Den Job `cablecar` in `ci-kotlin.yml` auskommentieren oder mit `if: false` deaktivieren, wenn du den Token nicht in GitHub hinterlegen willst.

### 3. Java-Version (Auftragsverwaltung / Kotlin)

- **ci-java.yml:** aktuell JDK 17 (z. B. auf 11 ändern: `java-version: '11'`).
- **ci-kotlin.yml:** JDK 11 für Gradle (SchimmenSpiel/CableCar nutzen JVM 11).

### 4. Python-Version

In **ci-python.yml** steht `python-version: '3.10'`. Du kannst `3.9` oder `3.11` setzen, je nachdem was deine Projekte brauchen.

### 5. KI-Handels-Framework: TA-Lib

Unter Linux wird `libta-lib-dev` installiert, damit `pip install -r requirements.txt` (inkl. TA-Lib) funktioniert. Wenn du TA-Lib weglässt oder nur Import-Checks ohne TA-Lib machst, kannst du den Schritt „Install system deps“ anpassen oder entfernen.

### 6. Eigene Schritte (Tests, Lint, Deploy)

- **Kotlin:** Nach `./gradlew build` kannst du z. B. `./gradlew test` oder Reports hochladen.
- **Java:** Nach `javac` z. B. JUnit-Tests mit Maven/Gradle, sobald du ein Build-Tool nutzt.
- **Python:** Statt nur Import-Check z. B. `pytest BA-Handschrifterkennung/` oder `pytest KI-Handels-Framework/`, sobald Tests vorhanden sind.

### 7. Path-Filter erweitern

Wenn du z. B. bei Änderungen an der Haupt-README auch alle Projekte bauen willst, kannst du unter `paths:` z. B. `README.md` ergänzen. Standard: Nur die jeweiligen Projektordner, damit CI schnell und gezielt läuft.

---

## Workflow manuell ausführen (optional)

Falls du einen Workflow per Klick starten willst:

```yaml
on:
  workflow_dispatch:
```

in den jeweiligen Workflow unter `on:` ergänzen. Dann erscheint unter **Actions** → Workflow auswählen → **Run workflow**.

---

## Kurz: Nötige Anpassungen für dich

1. **Branch:** `main`/`master` passt lassen oder auf deinen Default-Branch anpassen.
2. **CableCar:** Secret `SOPRA_GITLAB_TOKEN` in den Repo-Secrets anlegen (oder CableCar-Job deaktivieren).
3. **Rest:** Optional JDK-, Python-Version und weitere Schritte (Tests, Lint) nach Bedarf anpassen.
