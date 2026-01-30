import pandas as pd
import numpy as np # We'll need numpy

# ------------------------------------------------------------------
# 1) Load price data and pre-compute RSI
# ------------------------------------------------------------------
csv_file = "BTCUSDT15mF_with_rsi.csv"                     # adjust if needed
data = pd.read_csv(csv_file, parse_dates=True, index_col="Date")
data = data.loc["2020-01-01":"2025-04-30"].copy()

# ------------------------------------------------------------------
# 2) Helper that back-tests one (buy, sell) pair
# ------------------------------------------------------------------

def backtest_pair_vectorized(buy_thr: int, sell_thr: int,
                             df: pd.DataFrame,
                             capital: float = 1_000,
                             fee: float = 0.002) -> float:
    """Return total PnL using a faster, vectorized approach."""
    
    # 1. Generate signals
    # A '1' means we want to be in a position (RSI < buy_thr)
    # A '0' means we want to be out of a position (RSI > sell_thr)
    # NaN means we hold our current state
    signals = pd.Series(np.nan, index=df.index)
    signals[df["RSI_14"] < buy_thr] = 1.0
    signals[df["RSI_14"] > sell_thr] = 0.0

    # 2. Propagate signals forward to determine our position at any time
    # ffill() carries the last valid signal forward
    positions = signals.ffill().fillna(0) # Start with 0 position

    # 3. Find the trades
    # A trade happens when our position changes. We use diff() to find these points.
    # A change from 0 to 1 is a buy. A change from 1 to 0 is a sell.
    trades = positions.diff()

    # 4. Calculate PnL
    # Get the prices at which we bought and sold
    buy_prices = df.loc[trades == 1.0, "Close"]
    sell_prices = df.loc[trades == -1.0, "Close"]
    
    # Make sure we have the same number of buys and sells
    if len(buy_prices) > len(sell_prices):
        buy_prices = buy_prices[:-1] # Drop the last open trade
    
    if len(buy_prices) == 0:
        return 0.0

    # Calculate percentage gains and subtract fees
    pct_gains = (sell_prices.values - buy_prices.values) / buy_prices.values
    pnl = (pct_gains * capital) - (capital * fee)
    
    return pnl.sum()
# ------------------------------------------------------------------
# 3) Exhaustive grid search over RSI levels
# ------------------------------------------------------------------
results = []

for buy in range(10, 12):            # 10 … 40
    for sell in range(50, 53):       # 50 … 80
        if sell <= buy:              # skip illogical pairs
            continue
        pnl = backtest_pair_vectorized(buy, sell, data)
        results.append({"buy_rsi": buy, "sell_rsi": sell, "total_pnl": pnl})

results_df = pd.DataFrame(results)
results_df.to_csv("rsi_grid_results.csv", index=False)

print("Finished. Grid size:", len(results_df))
print("Top 5 by PnL:")
print(results_df.sort_values("total_pnl", ascending=False).head())

