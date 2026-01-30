import os
import requests
import zipfile
import pandas as pd
from datetime import datetime, timedelta
import tkinter as tk
from tkinter import messagebox

def download_zip_file(url, file_name, download_folder):
    print(f"Downloading {file_name}...")
    try:
        file_data = requests.get(url)
        file_data.raise_for_status()
    except requests.exceptions.RequestException as e:
        print(f"Error downloading {file_name}: {e}")
        return False
    with open(os.path.join(download_folder, file_name), 'wb') as f:
        f.write(file_data.content)
    print(f"{file_name} saved in '{download_folder}' folder.")
    return True

def extract_zip_file(file_name, download_folder, extract_folder):
    print(f"Extracting {file_name}...")
    with zipfile.ZipFile(os.path.join(download_folder, file_name), 'r') as z:
        z.extractall(extract_folder)
    print(f"{file_name} extracted to '{extract_folder}' folder.")

def generate_and_download_zip_files(base_url, trading_pair_name, timeframe, start_year, start_month, end_year, end_month):
    download_folder = 'downloads'
    extract_folder = f"{trading_pair_name}{timeframe}"
    os.makedirs(download_folder, exist_ok=True)
    os.makedirs(extract_folder, exist_ok=True)

    for year in range(start_year, end_year + 1):
        start_month_temp = 1 if year != start_year else start_month
        end_month_temp = 13 if year != end_year else end_month + 1
        for month in range(start_month_temp, end_month_temp):
            file_name = f"{trading_pair_name}-{timeframe}-{year}-{month:02}.zip"
            file_url = f"{base_url}{file_name}"
            if download_zip_file(file_url, file_name, download_folder):
                extract_zip_file(file_name, download_folder, extract_folder)

def generate_and_download_daily_zip_files(base_urld, trading_pair_name, timeframe, start_date, end_date):
    download_folder = 'downloads'
    extract_folder = f"{trading_pair_name}{timeframe}"
    os.makedirs(download_folder, exist_ok=True)
    os.makedirs(extract_folder, exist_ok=True)

    current_date = start_date
    while current_date <= end_date:
        file_name = f"{trading_pair_name}-{timeframe}-{current_date.strftime('%Y-%m-%d')}.zip"
        file_url = f"{base_urld}{file_name}"
        if download_zip_file(file_url, file_name, download_folder):
            extract_zip_file(file_name, download_folder, extract_folder)
        current_date += timedelta(days=1)

def combine_csv_files(folder_name):
    csv_files = [file for file in os.listdir(folder_name) if file.endswith('.csv')]
    dfs = [pd.read_csv(os.path.join(folder_name, file), header=None) for file in csv_files]
    result = pd.concat(dfs, ignore_index=True)
    result.columns = dfs[0].columns
    result.to_csv('calc.csv', index=False, header=False)

def process_calc_file(trading_pair_name, timeframe):
    df = pd.read_csv("calc.csv",
                     names=["Date", "Open", "High", "Low", "Close", "Volume", "T5", "T4", "T3", "T2", "T1", "ignore"])

    # Convert each timestamp individually based on its value
    def convert_timestamp(val):
        try:
            val = float(val)
            if val > 1e14:  # likely microseconds
                return pd.to_datetime(val, unit='us', errors='coerce')
            else:           # likely milliseconds
                return pd.to_datetime(val, unit='ms', errors='coerce')
        except:
            return pd.NaT

    df["Date"] = df["Date"].apply(convert_timestamp)
    df.drop(columns=['ignore', 'T1', 'T2', 'T3', 'T4', 'T5'], inplace=True)
    df.sort_values(by='Date', ascending=True, inplace=True)
    df.to_csv(f'{trading_pair_name}{timeframe}F.csv', index=False)

def main():
    window = tk.Tk()
    window.title("Data Downloader")

    tk.Label(window, text="Enter trading pair names (comma-separated):").grid(row=0, column=0)
    trading_pair_names_entry = tk.Entry(window)
    trading_pair_names_entry.grid(row=0, column=1)

    tk.Label(window, text="Enter timeframes (comma-separated):").grid(row=1, column=0)
    timeframe_entry = tk.Entry(window)
    timeframe_entry.grid(row=1, column=1)

    tk.Label(window, text="Enter start year :").grid(row=2, column=0)
    start_year_entry = tk.Entry(window)
    start_year_entry.grid(row=2, column=1)

    tk.Label(window, text="Enter end year :").grid(row=3, column=0)
    end_year_entry = tk.Entry(window)
    end_year_entry.grid(row=3, column=1)

    tk.Label(window, text="Enter start month :").grid(row=4, column=0)
    start_month_entry = tk.Entry(window)
    start_month_entry.grid(row=4, column=1)

    tk.Label(window, text="Enter end month :").grid(row=5, column=0)
    end_month_entry = tk.Entry(window)
    end_month_entry.grid(row=5, column=1)

    tk.Label(window, text="Enter daily start date (YYYY-MM-DD, optional):").grid(row=6, column=0)
    daily_start_date_entry = tk.Entry(window)
    daily_start_date_entry.grid(row=6, column=1)

    tk.Label(window, text="Enter daily end date (YYYY-MM-DD, optional):").grid(row=7, column=0)
    daily_end_date_entry = tk.Entry(window)
    daily_end_date_entry.grid(row=7, column=1)

    def on_submit():
        try:
            trading_pair_names = [name.strip().upper() for name in trading_pair_names_entry.get().split(',')]
            timeframes = [t.strip() for t in timeframe_entry.get().split(',')]
            start_year = int(start_year_entry.get())
            start_month = int(start_month_entry.get())
            end_year = int(end_year_entry.get())
            end_month = int(end_month_entry.get())

            daily_start_date = datetime.strptime(daily_start_date_entry.get(), "%Y-%m-%d") if daily_start_date_entry.get() else None
            daily_end_date = datetime.strptime(daily_end_date_entry.get(), "%Y-%m-%d") if daily_end_date_entry.get() else None

            for trading_pair_name in trading_pair_names:
                for timeframe in timeframes:
                    base_url = f"https://data.binance.vision/data/spot/monthly/klines/{trading_pair_name}/{timeframe}/"
                    base_urld = f"https://data.binance.vision/data/spot/daily/klines/{trading_pair_name}/{timeframe}/"

                    generate_and_download_zip_files(base_url, trading_pair_name, timeframe, start_year, start_month, end_year, end_month)
                    
                    if daily_start_date and daily_end_date:
                        generate_and_download_daily_zip_files(base_urld, trading_pair_name, timeframe, daily_start_date, daily_end_date)

                    combine_csv_files(f"{trading_pair_name}{timeframe}")
                    process_calc_file(trading_pair_name, timeframe)

            messagebox.showinfo("Success", "Data processing complete")

        except Exception as e:
            messagebox.showerror("Error", str(e))

    tk.Button(window, text="Submit", command=on_submit).grid(row=8, column=1)
    window.mainloop()

if __name__ == "__main__":
    main()

