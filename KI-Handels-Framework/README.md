# KI-Handels-Framework

Python-Skripte für Krypto-Handel: Binance-Daten herunterladen, RSI-Backtesting, Parameter-Optimierung und LSTM-Marktphasen-Klassifikation.

---

## Inhaltsverzeichnis

- [Übersicht der Skripte](#übersicht-der-skripte)
- [Voraussetzungen](#voraussetzungen)
- [Installation](#installation)
- [Ausführung](#ausführung)
- [Datenfluss](#datenfluss)
- [Hinweise](#hinweise)

---

## Übersicht der Skripte

| Datei | Beschreibung |
|-------|--------------|
| **binance_data_downloader.py** | GUI (Tkinter): Trading-Paare, Timeframes und Zeitraum eingeben; lädt Klines von data.binance.vision, entpackt ZIPs, kombiniert CSVs und erzeugt bereinigte Dateien (z. B. `BTCUSDT15mF.csv`). |
| **rsi_backtesting_engine.py** | RSI berechnen (TA-Lib), Backtest mit konfigurierbaren Buy/Sell-Schwellen; schreibt Trades in eine CSV. Wird von `rsi_parameter_optimizer.py` genutzt. |
| **rsi_strategy_backtester.py** | Standalone-Backtest: lädt CSV (z. B. `BTCUSDT1hF.csv`), RSI 30/70, Round-Trip-Fee, compounding; gibt Trades und Performance-Statistiken aus. |
| **rsi_parameter_grid_search.py** | Grid Search über RSI Buy/Sell-Schwellen; erwartet `BTCUSDT15mF_with_rsi.csv` mit Spalte `RSI_14`; schreibt `rsi_grid_results.csv`. |
| **rsi_parameter_optimizer.py** | Führt Backtests für viele RSI-Paare (Buy-/Sell-Bereiche) aus; nutzt `rsi_backtesting_engine.backtest_rsi_strategy`; schreibt pro Datei Ordner mit Trades und `optimization_results.csv`. |
| **lstm_market_phase_classifier.py** | LSTM-Klassifikation von Marktphasen: Sequenzen aus Features, Zeit-Split, Z-Score, Training mit Early Stopping, Evaluation (Accuracy, Konfusionsmatrix), Speicherung von Modell und Scaler (joblib). |

---

## Voraussetzungen

- **Python 3.8+**
- Abhängigkeiten siehe `requirements.txt`

**TA-Lib:** Unter Windows oft über vorkompilierte Wheels installieren (z. B. von [unofficial Windows binaries](https://www.lfd.uci.edu/~gohlke/pythonlibs/#ta-lib)) oder Conda: `conda install -c conda-forge ta-lib`.

---

## Installation

```bash
cd KI-Handels-Framework
pip install -r requirements.txt
```

Falls TA-Lib über pip nicht installierbar ist, siehe [Hinweise](#hinweise) unten.

---

## Ausführung

### 1. Daten herunterladen (Binance)

```bash
python binance_data_downloader.py
```

- GUI öffnen, Trading-Paare (z. B. `BTCUSDT`), Timeframes (z. B. `15m`, `1h`), Jahr/Monat und optional tägliche Daten eingeben.
- Nach „Submit“ werden ZIPs in `downloads/` geladen, entpackt, kombiniert und bereinigt.
- Ausgabe z. B.: `BTCUSDT15mF.csv`, `BTCUSDT1hF.csv` (Spalten: Date, Open, High, Low, Close, Volume).

### 2. RSI-Backtest (Engine)

Erwartet CSV mit Spalte `Date` (parsbar). Ausgabe: Trades-CSV.

```python
# Beispielaufruf aus anderem Skript:
from rsi_backtesting_engine import backtest_rsi_strategy
backtest_rsi_strategy("BTCUSDT15mF.csv", "trades.csv", rsi_buy_threshold=30, rsi_sell_threshold=70, rsi_period=14)
```

### 3. RSI-Strategie-Backtester (Standalone)

CSV-Datei und Zeitraum in der Datei anpassen (`csv_file`, `data['...']`), dann:

```bash
python rsi_strategy_backtester.py
```

### 4. RSI-Parameter-Optimierung

In `rsi_parameter_optimizer.py` die Liste `input_csvs` und die Bereiche `rsi_buy_range` / `rsi_sell_range` anpassen, dann:

```bash
python rsi_parameter_optimizer.py
```

Erzeugt pro CSV einen Ordner mit Trades und `optimization_results.csv`.

### 5. RSI-Grid-Search

In `rsi_parameter_grid_search.py` die Datei `BTCUSDT15mF_with_rsi.csv` bereitstellen (oder eigene CSV mit `RSI_14` und `Close`). Buy/Sell-Ranges im Skript anpassen, dann:

```bash
python rsi_parameter_grid_search.py
```

Ergebnisse in `rsi_grid_results.csv`.

### 6. LSTM-Marktphasen-Klassifikation

Skript enthält Dummy-Daten-Generierung; für echte Daten CSV/Features anbinden. Training, Evaluation und Speicherung von Modell/Scaler erfolgen im Skript.

```bash
python lstm_market_phase_classifier.py
```

---

## Datenfluss

1. **binance_data_downloader.py** → `BTCUSDT15mF.csv`, `BTCUSDT1hF.csv` (OHLCV).
2. **rsi_backtesting_engine** / **rsi_strategy_backtester** nutzen diese CSVs (mit RSI berechnet).
3. **rsi_parameter_optimizer** ruft die Engine für viele RSI-Paare auf.
4. **rsi_parameter_grid_search** nutzt eine CSV mit bereits berechneter `RSI_14`.
5. **lstm_market_phase_classifier** nutzt eigene Feature-Sequenzen (Dummy oder angepasste Daten).

---

## Hinweise

- **TA-Lib:** Unter Windows ggf. Wheel von Gohlke oder `conda install -c conda-forge ta-lib` verwenden.
- **Binance-Daten:** Keine API-Keys nötig; Nutzung von data.binance.vision unter Beachtung der Binance-Nutzungsbedingungen.
- **Backtest:** Historische Performance gibt keine Garantie für zukünftige Ergebnisse; Handelsgebühren und Slippage sind vereinfacht abgebildet.
- **LSTM:** Für Produktion Daten-Pipeline (Features, Labels) an echte Marktdaten anpassen.

---

