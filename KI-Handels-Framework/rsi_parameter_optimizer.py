import pandas as pd
from rsi_backtesting_engine import backtest_rsi_strategy
import os
def calculate_win_rate(trades):
    if len(trades) == 0:
        return 0
    winning_trades = sum(1 for trade in trades if trade['profit_percent'] > 0)
    return winning_trades / len(trades)

def run_tests(input_csv, output_dir, rsi_buy_range, rsi_sell_range, rsi_period=14, initial_capital=1000):
    results = []

    for rsi_buy_threshold in rsi_buy_range:
        for rsi_sell_threshold in rsi_sell_range:
            if rsi_buy_threshold >= rsi_sell_threshold:
                continue

            trades_csv = os.path.join(output_dir, f'trades_{rsi_buy_threshold}_{rsi_sell_threshold}.csv')
            backtest_rsi_strategy(input_csv, trades_csv, rsi_buy_threshold, rsi_sell_threshold, rsi_period)

            trades_df = pd.read_csv(trades_csv)
            trades_df['Year'] = pd.to_datetime(trades_df['exit_time']).dt.year
            trades = trades_df.to_dict('records')

            profit_percent_by_year = trades_df.groupby('Year')['profit_percent'].sum()
            win_rate_by_year = trades_df.groupby('Year').apply(lambda x: calculate_win_rate(x.to_dict('records')))
            total_profit_percent = sum(trade['profit_percent'] for trade in trades)
            win_rate = calculate_win_rate(trades)

            result = {
                'rsi_buy_threshold': rsi_buy_threshold,
                'rsi_sell_threshold': rsi_sell_threshold,
                'num_trades': len(trades),
            }

            for year in range(2021, 2024):
                result[f'total_profit_percent_{year}'] = profit_percent_by_year.get(year, 0)
                result[f'win_rate_{year}'] = win_rate_by_year.get(year, 0)

            result['total_profit_percent'] = total_profit_percent
            result['win_rate'] = win_rate

            results.append(result)

    results_df = pd.DataFrame(results)
    output_csv = os.path.join(output_dir, 'optimization_results.csv')
    results_df.to_csv(output_csv, index=False)
    print(f'Results saved to {output_csv}')

def run_tests_for_files(input_csvs, rsi_buy_range, rsi_sell_range, rsi_period=14, initial_capital=1000):
    for input_csv in input_csvs:
        output_dir = os.path.splitext(input_csv)[0]  # use filename without extension as directory name
        os.makedirs(output_dir, exist_ok=True)  # create directory if it doesn't exist

        run_tests(input_csv, output_dir, rsi_buy_range, rsi_sell_range, rsi_period)

def main():
    input_csvs = ['BTCUSDT15mF.csv']  # Replace with your list of CSV files
    rsi_buy_range = range(10, 49)  # Adjust the range as needed
    rsi_sell_range = range(50, 85)  # Adjust the range as needed
    rsi_period = 14

    run_tests_for_files(input_csvs, rsi_buy_range, rsi_sell_range, rsi_period)

if __name__ == '__main__':
    main()
