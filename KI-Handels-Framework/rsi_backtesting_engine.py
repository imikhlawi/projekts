import pandas as pd
import numpy as np
import talib
import os
from datetime import datetime

def calculate_rsi(data, period):
    rsi = talib.RSI(data['Close'], timeperiod=period)
    return rsi

def backtest_strategy(data, rsi_buy, rsi_sell, initial_capital=1000):
    position = None
    trades = []

    for index, row in data.iterrows():
        if position is None and row['RSI'] < rsi_buy:
            position = {
                'entry_time': row['Date'],
                'entry_price': row['Close'],
            }
        elif position is not None and row['RSI'] > rsi_sell:
            profit = row['Close'] - position['entry_price']
            profit_percent = (((profit / position['entry_price']) * 100) -0.2)
            position.update({
                'exit_time': row['Date'],
                'exit_price': row['Close'],
                'profit': profit,
                'profit_percent': profit_percent
            })
            trades.append(position)
            position = None

    return trades

def save_trades_to_csv(trades, filename):
    trades_df = pd.DataFrame(trades)
    trades_df.to_csv(filename, index=False)

def backtest_rsi_strategy(input_csv, output_csv, rsi_buy_threshold, rsi_sell_threshold, rsi_period=14):
    data = pd.read_csv(input_csv, parse_dates=['Date'])
    data['RSI'] = calculate_rsi(data, rsi_period)

    trades = backtest_strategy(data, rsi_buy_threshold, rsi_sell_threshold)
    save_trades_to_csv(trades, output_csv)
    print(f'Trades saved to {output_csv}')

