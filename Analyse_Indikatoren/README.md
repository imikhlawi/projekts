
# Analyse Indikatoren

**Pine Script v5 Indikatoren und eine vollständige Strategie für TradingView.**  
Struktur, Confluence, Volumen, VWAP, Oszillatoren und ein **Confluence-Score-Gauge** in einem Repository.

---

## Inhaltsverzeichnis

- [Schnellstart](#schnellstart)
- [Enthaltene Skripte](#enthaltene-skripte)
- [Highlight: Confluence Score Pro](#highlight-confluence-score-pro)
- [Technische Hinweise](#technische-hinweise)
- [Contributing & Lizenz](#contributing--lizenz)

---

## Schnellstart

1. [TradingView](https://www.tradingview.com/) öffnen und einen Chart laden.
2. Unten **Pine Editor** öffnen (*Indikatoren* → *Pine Editor* oder Tab „Pine Editor“).
3. Gewünschte `.pine`-Datei in diesem Repo öffnen, **gesamten Inhalt kopieren** und in den Editor einfügen.
4. Auf **„Zum Chart hinzufügen“** klicken.

Die Skripte laufen ausschließlich in der TradingView-Umgebung.

---

## Enthaltene Skripte

| Datei | Typ | Kurztitel | Beschreibung |
|-------|-----|-----------|--------------|
| **ACSE.pine** | **Strategie** | ACSE | Confluence-Strategie: MA, RSI, ADX, VWAP, optional Volatilität; Marktstruktur, Order Blocks, SL/TP/Trailing, On-Screen-Dashboard. |
| **Confluence_Score_Pro.pine** | Indikator | **CS Pro** | **Neu:** Ein Wert 0–100 als „Trade Readiness“. Gauge im eigenen Pane, Breakdown-Tabelle (MA, RSI, ADX, VWAP, Vol.), optional HTF. |
| **Moving_Average_Ribbon.pine** | Indikator | AMAR | Ribbon-, GMMA- und Dual-MA-Modus; SMA/EMA/WMA/HMA/ALMA/TEMA/DEMA. |
| **Market_Structure_Engine.pine** | Indikator | MSE | Swing-Punkte, BOS/CHoCH, Order Blocks, Fair Value Gaps, Daily/Weekly/Monthly High/Low. |
| **Oscillator_Suite.pine** | Indikator | UOS | RSI, Stochastic, CCI, MFI, MACD; Regular/Hidden Divergenzen; Dashboard; optional Higher Timeframe. |
| **Volume_VWAP_Engine.pine** | Indikator | VPVE | VWAP (Session/Week/Month/…), StdDev-Bänder; OBV + MA + Divergenz; Volumen-MA und Spike-Erkennung. |
| **Volatility_Matrix.pine** | Indikator | VTM | Bollinger, Keltner, Squeeze; MA-Ribbon; ADX/DMI; Parabolic SAR; Trend-Hintergrund. |

---

## Highlight: Confluence Score Pro

**Confluence_Score_Pro.pine** fasst mehrere Faktoren in **einen Score von 0 (bearish) bis 100 (bullish)** zusammen:

- **Gauge im eigenen Pane:** Balken 0–100 mit Farbcodierung (Rot / Grau / Grün) und Linien z. B. bei 25 / 50 / 75.
- **Breakdown-Tabelle auf dem Chart:** Zeigt, welche Komponenten erfüllt sind (MA, RSI, ADX, VWAP, optional Volumen) mit ✓/✗.
- **Optional Higher Timeframe:** Zeigt den Confluence-Score eines höheren Timeframes in der Tabelle.
- **Alerts:** z. B. bei Überschreiten von „Strong Long“ / Unterschreiten von „Strong Short“ oder Kreuzung der 50-Linie.

Ideal als **schneller Blick** auf Bias, ohne alle Einzelindikatoren zu prüfen. Kombinierbar mit ACSE oder anderen Skripten.

---

## Technische Hinweise

- **Version:** Alle Skripte nutzen **Pine Script v5** (`// @version=5`).
- **Dateien:** Quellcode liegt als **`.pine`** vor (Standard für Pine-Skripte).
- **Strategie:** Nur **ACSE.pine** ist eine **Strategie** (Backtest, Orders); alle anderen sind **Indikatoren**.

