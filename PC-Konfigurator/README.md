# PC-Konfigurator

Ein Eclipse-basiertes Tool zum Konfigurieren und Modellieren von PCs – entwickelt im **Universitätskontext** (z. B. SCE/Lehrveranstaltung zu modellgetriebener Entwicklung). Es besteht aus einer **Domain-Specific Language (DSL)** für PC-Komponenten (Xtext), **Generatoren** für Komponentendaten (aus einer Produktdatenquelle) und einem **visuellen Konfigurator** (Cinco) für PC- und Mainboard-Diagramme.

## Übersicht

| Komponente | Technologie | Beschreibung |
|------------|-------------|--------------|
| **Component DSL** | Xtext | Sprache für Komponentenbeschreibungen (CPU, RAM, Mainboard, Gehäuse, Laufwerk, Netzteil, GPU) in `.comp`-Dateien |
| **Component Generator** | Eclipse-Plugin, Xtend | Kontextmenü auf `.comp`-Dateien: Generiert Komponentenlisten aus dem Product-Service (`prodserv.jar`) |
| **PC-Konfigurator (Product)** | Cinco (MGL) | Graph-Editoren für PC- und Mainboard-Modelle mit Validierung und HTML-Export |

## Projektstruktur

```
PC-Konfigurator/
├── info.scce.cinco.fp.compdsl/          # Xtext-DSL (Grammatik, Generator-Stub, Scoping, Validator)
│   ├── src/.../ComponentDsl.xtext       # Grammatik für .comp-Dateien
│   ├── src/.../GenerateComponentDsl.mwe2
│   └── model/generated/                  # (wird generiert: Ecore, Parser, etc.)
├── info.scce.cinco.fp.compdsl.ide/      # IDE-Unterstützung (Content Assist, etc.)
├── info.scce.cinco.fp.compdsl.ui/       # UI-Integration (Editor, Quickfix)
├── info.scce.cinco.fp.compgen/          # Komponenten-Generatoren
│   ├── src/.../action/                  # Eclipse-Actions (Rechtsklick auf .comp)
│   ├── src/.../generator/               # CPU, RAM, GPU, Mainboard, Case, Drive, PowerSupply
│   └── lib/prodserv.jar                 # Externe API: ProductService + Descriptors
└── info.scce.cinco.product.fp.pcconfig/ # Cinco-Produkt (PC- & Mainboard-Editor)
    ├── model/                            # MGL-Definitionen, Style, CPD
    │   ├── PC.mgl, Mainboard.mgl
    │   ├── Configurator.cpd
    │   └── Configurator.style
    └── src/.../pc/, mb/                 # Hooks, Checks, HTML-Generatoren
```

## Voraussetzungen

- **Java 11** (Bundle-RequiredExecutionEnvironment: JavaSE-11)
- **Eclipse IDE** (empfohlen: Eclipse for RCP and RAP Developers oder ähnlich)
- **Xtext** (z. B. Xtext 2.22 oder kompatibel) – für die DSL
- **Cinco** (SCE Cinco Meta-Plugin) – für die Graph-Editoren PC und Mainboard

Die Projekte sind als **Eclipse PDE-Plugins** angelegt (kein Maven/Tycho im Repository).

## Einrichtung

### 1. Eclipse mit Xtext und Cinco

- Eclipse installieren und **Xtext** über die Eclipse-Update-Site oder das Marketplace-Plugin installieren.
- **Cinco** muss separat bezogen und in Eclipse installiert werden (siehe [Cinco](https://cinco.scce.info) / SCE für Installationsanleitung und Abhängigkeiten).

### 2. Repository klonen

```bash
git clone <repository-url>
cd PC-Konfigurator
```

### 3. Projekte importieren

- **File → Import → Existing Projects into Workspace**
- Root Directory auf den Ordner `PC-Konfigurator` zeigen
- Alle fünf Projekte auswählen und importieren

**Reihenfolge der Abhängigkeiten:**  
`compdsl` → `compdsl.ide` → `compdsl.ui`; `compgen` (abhängt von `prodserv.jar`); `pcconfig` (abhängt von `compdsl` und Cinco).

### 4. Xtext-Generierung (compdsl)

- Projekt `info.scce.cinco.fp.compdsl` im Explorer auswählen
- Rechtsklick → **Run As → MWE2 Workflow**  
  - Konfiguration: `GenerateComponentDsl.mwe2` (unter `src/.../GenerateComponentDsl.mwe2`)
- Danach sollten `src-gen/`, `xtend-gen/` und ggf. `model/generated/` befüllt sein

### 5. Cinco-Generierung (pcconfig)

- Im Projekt `info.scce.cinco.product.fp.pcconfig` die Cinco-Generierung für die MGL-Dateien und das CPD ausführen (je nach Cinco-Version: Generator aus dem Cinco-Menü oder über konfigurierten Build).
- Dadurch werden u. a. `src-gen/`, `plugin.xml`, `plugin.properties` und weitere generierte Artefakte erzeugt.

### 6. prodserv.jar

- Das Plugin `info.scce.cinco.fp.compgen` erwartet die JAR **`lib/prodserv.jar`** im eigenen Projektordner.
- Diese JAR stellt u. a. bereit:
  - `info.scce.cinco.fp.prodserv.ProductService` (Methoden: `CPUs`, `mainboards`, `RAMs`, `GPUs`, `drives`, `cases`, `powerSupplies`)
  - Descriptor-Klassen in `info.scce.cinco.fp.prodserv.descriptor.*` (z. B. `CPUDescriptor`, `MainboardDescriptor`, …)
- Liegt `prodserv.jar` nicht vor, muss sie beschafft und unter `info.scce.cinco.fp.compgen/lib/prodserv.jar` abgelegt werden.

## Ausführen

- **Eclipse Application** starten (Run Configuration: Eclipse Application), sofern eine entsprechende Product-/Feature-Konfiguration mit Cinco und den hier enthaltenen Plugins existiert.
- Alternativ: Ein **Cinco-Product** verwenden, das die Plugins `info.scce.cinco.fp.compdsl*`, `info.scce.cinco.fp.compgen` und `info.scce.cinco.product.fp.pcconfig` enthält.

## Nutzung

1. **Komponenten generieren**  
   - Eine `.comp`-Datei anlegen oder öffnen.  
   - Rechtsklick auf die Datei → **Components Generator** → z. B. **Generate CPUs**, **Generate Mainboards**, **Generate RAMs** usw.  
   - Der Inhalt der Datei wird mit den vom `ProductService` gelieferten Komponenten (Format der DSL) gefüllt bzw. überschrieben.

2. **PC konfigurieren**  
   - Ein PC-Diagramm (`.pc`) anlegen.  
   - Case-Container aus der Komponenten-Palette bzw. aus referenzierten `.comp`-Dateien platzieren; darin Mainboard, PSU, Laufwerke usw. konfigurieren.  
   - Mainboard-Diagramme (`.mb`) für Mainboards mit CPU-, RAM- und GPU-Slots verwenden.

3. **Validierung**  
   - Die hinterlegten Checks (z. B. Socket-Typ, RAM-Typ, FormFactor, Leistungsaufnahme, Anzahl Laufwerke) melden Fehler direkt im Editor.

4. **HTML exportieren**  
   - Über die Cinco-Generatoren (z. B. „html-pc-gen“, „html-mb-gen“) können PC- bzw. Mainboard-Details als HTML exportiert werden.

## DSL – Komponententypen (.comp)

Die Dateiendung ist **`.comp`**. Ein Modell ist eine Menge von Komponenten in geschweiften Klammern:

- **CPUDescriptor** – name, type, powerConsumption, socket, price  
- **RAMDescriptor** – name, type (DDR3/DDR4), capacity, powerConsumption, price  
- **MainboardDescriptor** – name, socket, chipset, typeMemorySlots, numMemorySlots, numPCIe16Slots, numSataPorts, powerConsumption, formFactor, price  
- **CaseDescriptor** – name, formFactor, internalSlots, externalSlots, price  
- **DriveDescriptor** – name, type (HardDrive/OpticalDrive), description, powerConsumption, price  
- **PowerSupplyDescriptor** – name, power, price  
- **GPUDescriptor** – name, chip, memory, powerConsumption, price  

Enums: **RamType** (DDR3, DDR4), **SocketType** (z. B. Socket1150, Socket1151, SocketAM3), **FormFactor** (ATX, MicroATX, MiniITX), **DriveType** (HardDrive, OpticalDrive).

## Lizenz und Beiträge

- Lizenz bitte im Repository ergänzen (z. B. LICENSE-Datei).  
- Beiträge: Änderungen per Pull Request einreichen; bei größeren Änderungen vorher Issue oder Diskussion anlegen.

