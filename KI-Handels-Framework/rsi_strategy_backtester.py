import pandas as pd
import numpy as np
import talib
import matplotlib.pyplot as plt
from datetime import time

# ─────────────────────────────────────────────────────────────
# 1) Load data
# ─────────────────────────────────────────────────────────────
csv_file = 'BTCUSDT1hF.csv'
data = pd.read_csv(csv_file, parse_dates=True, index_col='Date')
data = data['2018-01-01':'2025-04-30']

# ─────────────────────────────────────────────────────────────
# 2) Indicators & signals
# ─────────────────────────────────────────────────────────────
data['RSI_14']   = talib.RSI(data['Close'], timeperiod=14)
data['buy_signal']  = data['RSI_14'] < 30
data['sell_signal'] = data['RSI_14'] > 70

# ─────────────────────────────────────────────────────────────
# 3) Back-test loop with compounding
# ─────────────────────────────────────────────────────────────
initial_capital = 1_000
fee_rate        = 0.002          # 0.2 % round-trip

trades = pd.DataFrame(columns=[
    'Entry Time', 'Entry Price', 'Exit Time', 'Exit Price',
    'Profit (%)', 'Profit ($)',
    'Drawdown (%)', 'Equity Before', 'Equity After'
])

equity        = initial_capital
open_position = None

for idx, row in data.iterrows():

    # ― Entry ―
    if row['buy_signal'] and open_position is None:
        open_position = {
            'Entry Time'   : idx,
            'Entry Price'  : row['Close'],
            'Equity Before': equity
        }

    # ― Exit ―
    elif row['sell_signal'] and open_position is not None:
        open_position['Exit Time']  = idx
        open_position['Exit Price'] = row['Close']

        pct_move      = (open_position['Exit Price'] - open_position['Entry Price']) / open_position['Entry Price']
        pct_after_fee = pct_move - fee_rate
        profit_dollar = pct_after_fee * open_position['Equity Before']
        equity       += profit_dollar                              # ← compounding

        lows = data.loc[open_position['Entry Time']:idx, 'Low']
        drawdown_pct = (lows.min() - open_position['Entry Price']) / open_position['Entry Price'] * 100

        open_position['Profit (%)']   = pct_after_fee * 100
        open_position['Profit ($)']   = profit_dollar
        open_position['Drawdown (%)'] = drawdown_pct
        open_position['Equity After'] = equity

        trades = pd.concat([trades, pd.DataFrame([open_position])], ignore_index=True)
        open_position = None

# ─────────────────────────────────────────────────────────────
# 4) Build the equity curve
# ─────────────────────────────────────────────────────────────
if len(trades):
    equity_curve = pd.concat(
        [pd.Series([initial_capital]), trades['Equity After']],
        ignore_index=True
    )
else:
    equity_curve = pd.Series([initial_capital])

# ─────────────────────────────────────────────────────────────
# 5) Performance statistics
# ─────────────────────────────────────────────────────────────
net_profit_pct = (equity_curve.iat[-1] - initial_capital) / initial_capital * 100
running_max    = equity_curve.cummax()
max_drawdown_pct = ((equity_curve - running_max) / running_max * 100).min()

total_trades = len(trades)
win_rate     = (trades['Profit ($)'] > 0).mean() * 100

gross_profit  = trades.loc[trades['Profit ($)'] > 0, 'Profit ($)'].sum()
gross_loss    = trades.loc[trades['Profit ($)'] < 0, 'Profit ($)'].abs().sum()
profit_factor = gross_profit / gross_loss if gross_loss else np.nan
return_to_dd  = net_profit_pct / abs(max_drawdown_pct) if max_drawdown_pct else np.nan

# ─────────────────────────────────────────────────────────────
# 6) Output summary
# ─────────────────────────────────────────────────────────────
print("\n===== Executed Trades =====")
print(trades)

print("\n===== Performance Summary (2018-01-01 → 2025-04-30) =====")
print(f"Total Net Profit:          {net_profit_pct:8.2f} %")
print(f"Maximum Drawdown:          {max_drawdown_pct:8.2f} %")
print(f"Return-to-Drawdown Ratio:   {return_to_dd:8.2f}")
print(f"Profit Factor:             {profit_factor:8.2f}")
print(f"Total Number of Trades:    {total_trades:8d}")
print(f"Win Rate:                  {win_rate:8.2f} %")

# ─────────────────────────────────────────────────────────────
# 7) Plot & save the equity curve  (trade # + exit date, 6 labels)
# ─────────────────────────────────────────────────────────────

N_LABELS = 6                                   # how many x-tick labels to show
points    = np.linspace(0, len(equity_curve)-1,
                        min(N_LABELS, len(equity_curve)),
                        dtype=int)

# Build formatted labels only at the chosen points
labels = []
for p in points:
    if p == 0:
        # starting balance → date = first bar in the slice
        date_str = pd.to_datetime(data.index[0]).strftime('%Y-%m-%d')
        labels.append(f"0 | {date_str}")
    else:
        # trade exit #p → use its Exit Time
        exit_ts  = trades['Exit Time'].iloc[p-1]
        date_str = pd.to_datetime(exit_ts).strftime('%Y-%m-%d')
        labels.append(f"{p} | {date_str}")

plt.figure(figsize=(12, 6))
plt.plot(equity_curve.values)
plt.title('Equity Curve (Compounded)')
plt.xlabel('Trade # | Exit Date')
plt.ylabel('Account Equity ($)')
plt.grid(True)

# Apply sparse ticks
plt.xticks(ticks=points, labels=labels, rotation=45, ha='right')

plt.tight_layout()
plt.savefig('equity_curve.png')
plt.show()

# ─────────────────────────────────────────────────────────────
# 8) Save CSV outputs
# ─────────────────────────────────────────────────────────────
trades.to_csv('trades.csv', index=False)
data.to_csv('modified_data.csv')

