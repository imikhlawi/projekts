# Preamble: Import all necessary libraries
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
import os # For checking file existence and saving models
import pandas as pd # Used in dummy data generation
import joblib # <<< MODIFICATION 1: IMPORT JOBLIB TO SAVE THE SCALER
matplotlib.use("Agg") # Use a non-interactive backend for saving plots on servers
# --- Re-using necessary helper classes/functions from previous steps ---

# From Instruction 2.1: Create Final Input Sequences
def create_sequences(
    df: pd.DataFrame,
    feature_names: list,
    label_name: str,
    sequence_length: int
) -> tuple[np.ndarray, np.ndarray]:
    """
    Transforms a flat time series DataFrame into sequences suitable for an LSTM model.
    """
    if not isinstance(df, pd.DataFrame): raise TypeError("df must be a pandas DataFrame.")
    if not isinstance(feature_names, list) or not all(isinstance(f, str) for f in feature_names): raise TypeError("feature_names must be a list of strings.")
    if not isinstance(label_name, str): raise TypeError("label_name must be a string.")
    if not isinstance(sequence_length, int) or sequence_length <= 0: raise ValueError("sequence_length must be a positive integer.")

    all_required_cols = feature_names + [label_name]
    missing_cols = [col for col in all_required_cols if col not in df.columns]
    if missing_cols: raise ValueError(f"Missing columns in DataFrame: {missing_cols}")

    feature_data = df[feature_names].values
    label_data = df[label_name].values

    X, y = [], []
    for i in range(len(df) - sequence_length):
        features = feature_data[i : i + sequence_length]
        label = label_data[i + sequence_length]
        X.append(features)
        y.append(label)

    X_sequences = np.array(X)
    y_labels = np.array(y)
    return X_sequences, y_labels

# From Instruction 2.2: Time-Aware Data Splitting
def time_aware_split(
    X_sequences: np.ndarray,
    y_labels: np.ndarray,
    train_pct: float = 0.7,
    val_pct: float = 0.15
) -> tuple[np.ndarray, np.ndarray, np.ndarray, np.ndarray, np.ndarray, np.ndarray]:
    """
    Splits generated sequences into training, validation, and test sets chronologically.
    """
    if not (0 < train_pct < 1 and 0 <= val_pct < 1 and (train_pct + val_pct) <= 1):
        raise ValueError("Invalid split percentages. Ensure 0 < train_pct < 1, 0 <= val_pct < 1, and (train_pct + val_pct) <= 1.")
    
    total_samples = len(X_sequences)
    if total_samples != len(y_labels):
        raise ValueError("X_sequences and y_labels must have the same number of samples.")
    if total_samples == 0:
        return (np.array([]),)*6

    train_end_idx = int(total_samples * train_pct)
    val_end_idx = int(total_samples * (train_pct + val_pct))

    X_train, y_train = X_sequences[:train_end_idx], y_labels[:train_end_idx]
    X_val, y_val = X_sequences[train_end_idx:val_end_idx], y_labels[train_end_idx:val_end_idx]
    X_test, y_test = X_sequences[val_end_idx:], y_labels[val_end_idx:]

    return X_train, y_train, X_val, y_val, X_test, y_test

# From Instruction 2.2 (Re-numbered in prompt): Feature Scaling (Z-score Normalization)
from sklearn.preprocessing import StandardScaler # Specific import for this function
def scale_features_zscore(
    X_train: np.ndarray,
    X_val: np.ndarray,
    X_test: np.ndarray
) -> tuple[np.ndarray, np.ndarray, np.ndarray, StandardScaler]: # Added StandardScaler to the return type hint
    """
    Scales input features (X_train, X_val, X_test) using StandardScaler (Z-score normalization).
    """
    if X_train.ndim != 3 or X_val.ndim != 3 or X_test.ndim != 3:
        raise ValueError("Input arrays must be 3-dimensional (num_samples, sequence_length, num_features).")
    
    num_features = X_train.shape[-1]
    if not (X_val.shape[-1] == num_features and X_test.shape[-1] == num_features):
        raise ValueError("Number of features (last dimension) must be consistent across all X arrays.")

    X_train_reshaped = X_train.reshape(-1, num_features)
    scaler = StandardScaler()
    scaler.fit(X_train_reshaped)

    X_train_scaled_reshaped = scaler.transform(X_train_reshaped)
    X_val_scaled_reshaped = scaler.transform(X_val.reshape(-1, num_features))
    X_test_scaled_reshaped = scaler.transform(X_test.reshape(-1, num_features))

    X_train_scaled = X_train_scaled_reshaped.reshape(X_train.shape)
    X_val_scaled = X_val_scaled_reshaped.reshape(X_val.shape)
    X_test_scaled = X_test_scaled_reshaped.reshape(X_test.shape)

    return X_train_scaled, X_val_scaled, X_test_scaled, scaler

# From Instruction 2.3: PyTorch Custom Dataset Class
class MarketPhaseDataset(Dataset):
    """
    A custom PyTorch Dataset class to handle time series feature sequences (X)
    and their corresponding classification labels (y).
    """
    def __init__(self, X: np.ndarray, y: np.ndarray):
        if X.ndim != 3:
            raise ValueError("X must be a 3D NumPy array (num_samples, sequence_length, num_features).")
        if y.ndim != 1:
            raise ValueError("y must be a 1D NumPy array (num_samples,).")
        if len(X) != len(y):
            raise ValueError("X and y must have the same number of samples.")

        self.X = torch.tensor(X, dtype=torch.float32)
        self.y = torch.tensor(y, dtype=torch.long) # Labels for CrossEntropyLoss should be long

    def __len__(self) -> int:
        return len(self.X)

    def __getitem__(self, idx: int) -> tuple[torch.Tensor, torch.Tensor]:
        return self.X[idx], self.y[idx]

# From Instruction 3.1: Define the LSTM Classifier Model
class LSTMClassifier(nn.Module):
    """
    A stacked LSTM model for classification tasks.
    """
    def __init__(self, input_size: int, hidden_size: int, num_layers: int, num_classes: int, dropout_rate: float):
        super(LSTMClassifier, self).__init__()
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.num_classes = num_classes

        lstm_dropout = dropout_rate if num_layers > 1 else 0
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True, dropout=lstm_dropout)
        self.dropout = nn.Dropout(dropout_rate)
        self.fc = nn.Linear(hidden_size, num_classes)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        h0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(x.device)
        c0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(x.device)

        lstm_out, (h_n, c_n) = self.lstm(x, (h0, c0))
        final_hidden_state = h_n[-1, :, :] # Get hidden state from the last layer

        dropped_out_state = self.dropout(final_hidden_state)
        logits = self.fc(dropped_out_state)

        return logits


# --- Instruction 4.2 & 4.3: Implement the Training & Validation Loop Function ---
# --- MODIFIED FUNCTION ---
def train_model(
    model: nn.Module,
    train_loader: DataLoader,
    val_loader: DataLoader,
    criterion: nn.Module,
    optimizer: optim.Optimizer,
    scheduler: optim.lr_scheduler._LRScheduler, # Add scheduler as an argument
    num_epochs: int,
    patience: int, # Add patience as an argument
    device: torch.device,
    save_path: str
) -> dict:
    """
    Trains and validates a PyTorch model, performing checkpointing and tracking history.
    Includes learning rate scheduling and early stopping.
    """
    model.to(device)
    best_val_loss = float('inf')
    epochs_no_improve = 0 # Counter for early stopping

    # Initialize history lists
    train_loss_history, val_loss_history = [], []
    train_acc_history, val_acc_history = [], []

    print(f"\n--- Starting Training on {device} ---")
    for epoch in range(1, num_epochs + 1):
        # Training Phase
        model.train()
        running_train_loss = 0.0
        correct_train_predictions = 0
        total_train_samples = 0

        for batch_X, batch_y in train_loader:
            batch_X, batch_y = batch_X.to(device), batch_y.to(device)
            optimizer.zero_grad()
            outputs = model(batch_X)
            loss = criterion(outputs, batch_y)
            loss.backward()
            optimizer.step()
            running_train_loss += loss.item() * batch_X.size(0)
            _, predicted = torch.max(outputs.data, 1)
            total_train_samples += batch_y.size(0)
            correct_train_predictions += (predicted == batch_y).sum().item()

        avg_train_loss = running_train_loss / total_train_samples
        avg_train_accuracy = correct_train_predictions / total_train_samples

        # Validation Phase
        model.eval()
        running_val_loss = 0.0
        correct_val_predictions = 0
        total_val_samples = 0

        with torch.no_grad():
            for batch_X_val, batch_y_val in val_loader:
                batch_X_val, batch_y_val = batch_X_val.to(device), batch_y_val.to(device)
                outputs_val = model(batch_X_val)
                loss_val = criterion(outputs_val, batch_y_val)
                running_val_loss += loss_val.item() * batch_X_val.size(0)
                _, predicted_val = torch.max(outputs_val.data, 1)
                total_val_samples += batch_y_val.size(0)
                correct_val_predictions += (predicted_val == batch_y_val).sum().item()

        avg_val_loss = running_val_loss / total_val_samples
        avg_val_accuracy = correct_val_predictions / total_val_samples

        # Epoch-End Reporting, History, and Checkpointing
        train_loss_history.append(avg_train_loss)
        val_loss_history.append(avg_val_loss)
        train_acc_history.append(avg_train_accuracy)
        val_acc_history.append(avg_val_accuracy)

        print(f"Epoch [{epoch}/{num_epochs}] | "
              f"Train Loss: {avg_train_loss:.4f}, Train Acc: {avg_train_accuracy:.4f} | "
              f"Val Loss: {avg_val_loss:.4f}, Val Acc: {avg_val_accuracy:.4f}")

        # --- NEW LOGIC FOR SCHEDULING AND EARLY STOPPING ---
        # 1. Step the scheduler based on validation loss
        scheduler.step(avg_val_loss)

        # 2. Check for improvement and save model
        if avg_val_loss < best_val_loss:
            print(f"  Validation loss improved ({best_val_loss:.4f} --> {avg_val_loss:.4f}). Saving model...")
            best_val_loss = avg_val_loss
            torch.save(model.state_dict(), save_path)
            epochs_no_improve = 0 # Reset counter
        else:
            epochs_no_improve += 1 # Increment counter
            print(f"  Validation loss did not improve. Patience: {epochs_no_improve}/{patience}")

        # 3. Check for early stopping
        if epochs_no_improve >= patience:
            print(f"\n--- Early stopping triggered after {patience} epochs with no improvement. ---")
            break # Exit the training loop
            
    print("--- Training Finished ---")
    return {
        'train_loss': train_loss_history,
        'val_loss': val_loss_history,
        'train_acc': train_acc_history,
        'val_acc': val_acc_history
    }

# --- Instruction 5.1 & 5.2: Create a Comprehensive Evaluation Function and Plotting Utility ---
def evaluate_model(model: nn.Module, data_loader: DataLoader, device: torch.device) -> dict:
    """
    Evaluates the model on a given DataLoader and calculates comprehensive metrics.

    Parameters
    ----------
    model : nn.Module
        The trained PyTorch model.
    data_loader : DataLoader
        DataLoader for the data split to evaluate (e.g., test_loader).
    device : torch.device
        The device the model is on.

    Returns
    -------
    dict
        A dictionary containing accuracy, classification report (as dict), and confusion matrix.
    """
    model.eval() # Set model to evaluation mode
    all_predictions = []
    all_true_labels = []

    with torch.no_grad(): # Disable gradient calculations
        for batch_X, batch_y in data_loader:
            batch_X, batch_y = batch_X.to(device), batch_y.to(device)
            outputs = model(batch_X)
            
            _, predicted = torch.max(outputs.data, 1) # Get predicted class indices
            
            all_predictions.extend(predicted.cpu().numpy()) # Move to CPU and convert to NumPy
            all_true_labels.extend(batch_y.cpu().numpy())

    # Convert lists to NumPy arrays
    true_labels_np = np.array(all_true_labels)
    predictions_np = np.array(all_predictions)

    # Calculate metrics
    accuracy = accuracy_score(true_labels_np, predictions_np)
    # classification_report returns string by default, output_dict=True for a dict
    report = classification_report(true_labels_np, predictions_np, output_dict=True)
    cm = confusion_matrix(true_labels_np, predictions_np)

    return {
        'accuracy': accuracy,
        'classification_report': report,
        'confusion_matrix': cm,
        'true_labels': true_labels_np, # Keep these for flexibility if needed
        'predictions': predictions_np
    }

def plot_confusion_matrix(cm: np.ndarray, class_names: list, title: str = 'Confusion Matrix') -> None:
    """
    Plots a confusion matrix using seaborn.

    Parameters
    ----------
    cm : np.ndarray
        The confusion matrix array.
    class_names : list
        List of class names (strings) corresponding to the labels.
    title : str, optional
        Title of the plot (default: 'Confusion Matrix').
    """
    plt.figure(figsize=(8, 6))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=class_names, yticklabels=class_names)
    plt.title(title)
    plt.xlabel('Predicted Label')
    plt.ylabel('True Label')


# Helper function to plot training history
def plot_training_history(history: dict):
    """Plots training and validation loss and accuracy history."""
    epochs = range(1, len(history['train_loss']) + 1)

    plt.figure(figsize=(12, 5))

    # Plot Loss
    plt.subplot(1, 2, 1)
    plt.plot(epochs, history['train_loss'], label='Train Loss')
    plt.plot(epochs, history['val_loss'], label='Validation Loss')
    plt.title('Training & Validation Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend()
    plt.grid(True)

    # Plot Accuracy
    plt.subplot(1, 2, 2)
    plt.plot(epochs, history['train_acc'], label='Train Accuracy')
    plt.plot(epochs, history['val_acc'], label='Validation Accuracy')
    plt.title('Training & Validation Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.legend()
    plt.grid(True)

    plt.tight_layout()
    plt.savefig("training_history333.png", dpi=150)
    plt.close()   


# --- Main execution block ---
if __name__ == "__main__":
    # --- Data Loading or Dummy Data Generation ---
    print("Attempting to load data from .npy files...")
    data_files_exist = (
        os.path.exists('X_train_scaled.npy') and
        os.path.exists('y_train.npy') and
        os.path.exists('X_val_scaled.npy') and
        os.path.exists('y_val.npy') and
        os.path.exists('X_test_scaled.npy') and
        os.path.exists('y_test.npy')
    )

    if data_files_exist:
        X_train_scaled = np.load('X_train_scaled.npy')
        y_train = np.load('y_train.npy')
        X_val_scaled = np.load('X_val_scaled.npy')
        y_val = np.load('y_val.npy')
        X_test_scaled = np.load('X_test_scaled.npy')
        y_test = np.load('y_test.npy')
        print("Data loaded successfully from .npy files.")
    else:
        # If .npy files are not found, load the raw CSV and process it from scratch.
        print("Required .npy files not found. Starting data processing from CSV.")
        csv_file_path = 'BTC_with_indicators.csv'
        sequence_length = 168
        feature_columns = ['ROC_14', 'CCI_20', 'STOCH_k', 'MACD', 'BB_Upper', 'volume', 'EMA_200', 'close_denoised', 'MACD_Hist', 'Price_vs_EMA200', 'HL_diff', 'ROC_10', 'ATR_14']

        label_column = 'MarketPhase'

        if not os.path.exists(csv_file_path):
            print(f"FATAL ERROR: The file '{csv_file_path}' was not found.")
            exit()
            
        df = pd.read_csv(csv_file_path)
        print(f"Successfully loaded '{csv_file_path}'. Shape: {df.shape}")
        
        print(f"Creating sequences with length {sequence_length}...")
        X_sequences, y_labels = create_sequences(df, feature_columns, label_column, sequence_length)

        print("Splitting data into train, validation, and test sets...")
        X_train, y_train, X_val, y_val, X_test, y_test = time_aware_split(X_sequences, y_labels, train_pct=0.7, val_pct=0.15)
        
        if len(X_train) == 0 or len(X_val) == 0:
            print("ERROR: Training or validation split is empty.")
            exit()


        print("Scaling features using Z-score normalization...")
        # <<< MODIFICATION 3: CAPTURE THE RETURNED SCALER AND SAVE IT
        # The function now returns 4 items. We capture the scaler in the 'fitted_scaler' variable.
        X_train_scaled, X_val_scaled, X_test_scaled, fitted_scaler = scale_features_zscore(X_train, X_val, X_test)
        
        # Now, save the captured scaler immediately after it's created and fitted.
        SCALER_FILENAME = 'scaler.gz'
        joblib.dump(fitted_scaler, SCALER_FILENAME)
        print(f"IMPORTANT: The fitted scaler has been saved to '{SCALER_FILENAME}'.")
        # <<< END OF MODIFICATION
        
        
        print("\nData processing from CSV is complete.")
        # Optional save can be uncommented if needed
        # np.save('X_train_scaled.npy', X_train_scaled)
        # np.save('y_train.npy', y_train)
        # np.save('X_val_scaled.npy', X_val_scaled)
        # np.save('y_val.npy', y_val)
        # np.save('X_test_scaled.npy', X_test_scaled)
        # np.save('y_test.npy', y_test)
        # print("Processed .npy files have been saved.")

    # --- Set Up the Training Environment ---
    print("\n--- Setting up training environment ---")

    ### MODIFIED ### - New simplified config to establish a baseline
    config = {
        'learning_rate': 0.0005,
        'num_epochs': 100,
        'model_save_path': 'best_lstm_baseline_model.pth',
        'batch_size': 256,
        'hidden_size': 16,           # Reduced model width
        'num_layers': 1,              # Single layer is the simplest possible
        'dropout_rate': 0.2,          # Moderate dropout
        'weight_decay': 0.001,         # Reasonable L2 penalty
        'patience': 10,                # Shorter patience for faster iteration
        'num_classes': 3
    }

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"Using device: {device}")

    train_dataset = MarketPhaseDataset(X_train_scaled, y_train)
    val_dataset = MarketPhaseDataset(X_val_scaled, y_val)
    test_dataset = MarketPhaseDataset(X_test_scaled, y_test)

    num_workers = 4 if device.type == 'cuda' else 0
    train_loader = DataLoader(train_dataset, batch_size=config['batch_size'], shuffle=True, num_workers=num_workers, pin_memory=True)
    val_loader = DataLoader(val_dataset, batch_size=config['batch_size'], shuffle=False, num_workers=num_workers, pin_memory=True)
    test_loader = DataLoader(test_dataset, batch_size=config['batch_size'], shuffle=False, num_workers=num_workers, pin_memory=True)
    
    print(f"Train/Val/Test Dataset sizes: {len(train_dataset)} / {len(val_dataset)} / {len(test_dataset)}")
    print(f"Number of classes detected: {config['num_classes']}")

    input_size = X_train_scaled.shape[-1]
    model = LSTMClassifier(
        input_size=input_size,
        hidden_size=config['hidden_size'],
        num_layers=config['num_layers'],
        num_classes=config['num_classes'],
        dropout_rate=config['dropout_rate']
    )
    model.to(device)

    ### NEW ### - Calculate class weights to combat imbalance
    print("Calculating class weights for the loss function...")
    from sklearn.utils.class_weight import compute_class_weight
    class_weights = compute_class_weight(
        class_weight='balanced',
        classes=np.unique(y_train),
        y=y_train
    )
    class_weights_tensor = torch.tensor(class_weights, dtype=torch.float32).to(device)
    print(f"Calculated weights: {class_weights_tensor}")

    ### MODIFIED ### - Apply weights to the loss function
    criterion = nn.CrossEntropyLoss(weight=class_weights_tensor)
    print("Loss function updated with class weights.")
    
    optimizer = optim.Adam(
        model.parameters(), 
        lr=config['learning_rate'], 
        weight_decay=config['weight_decay']
    )
    scheduler = optim.lr_scheduler.ReduceLROnPlateau(
        optimizer, 
        'min',
        patience=config['patience'] // 2, # Reduce LR if val_loss doesn't improve for ~3 epochs
        factor=0.2, # Less aggressive reduction
        #verbose=False # Set to False as it was causing issues on your server
    )
    
    history = train_model(
        model=model,
        train_loader=train_loader,
        val_loader=val_loader,
        criterion=criterion,
        optimizer=optimizer,
        scheduler=scheduler,
        num_epochs=config['num_epochs'],
        patience=config['patience'],
        device=device,
        save_path=config['model_save_path']
    )

    plot_training_history(history)

    # --- Final Evaluation on the Test Set ---
    print("\n--- Starting Final Evaluation on Test Set ---")

    best_model = LSTMClassifier(
        input_size=input_size,
        hidden_size=config['hidden_size'],
        num_layers=config['num_layers'],
        num_classes=config['num_classes'],
        dropout_rate=config['dropout_rate']
    )
    if os.path.exists(config['model_save_path']):
        best_model.load_state_dict(torch.load(config['model_save_path'], map_location=device))
        print(f"Best model loaded from {config['model_save_path']}")
    else:
        print(f"Warning: Best model checkpoint '{config['model_save_path']}' not found.")
        best_model = model
    
    best_model.to(device)

    test_metrics = evaluate_model(best_model, test_loader, device)

    print(f"\nTest Accuracy: {test_metrics['accuracy']:.4f}")
    
    print("\n--- Classification Report (Test Set) ---")
    class_names = ['Bearish', 'Sideways', 'Bullish'] if config['num_classes'] == 3 else [f'Class {i}' for i in range(config['num_classes'])]
    
    print(classification_report(test_metrics['true_labels'], test_metrics['predictions'], target_names=class_names, zero_division=0))

    print("\n--- Plotting Confusion Matrix (Test Set) ---")
    plot_confusion_matrix(test_metrics['confusion_matrix'], class_names=class_names, title='Confusion Matrix - Test Set')

    print("\n--- End of Script ---")
