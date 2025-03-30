from flask import Flask, request, jsonify
import pickle
import numpy as np
import os
from werkzeug.utils import secure_filename
import librosa

app = Flask(__name__)

# Allowed file extensions
ALLOWED_EXTENSIONS = {'wav', 'mp3'}

# Path to the model file
MODEL_PATH = 'motorsoundsmodel_v2.pkl'

# Load the trained model
with open(MODEL_PATH, 'rb') as file:
    model = pickle.load(file)

# Check if uploaded file is allowed
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/')
def home():
    return "Flask server is running"

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)
        file_path = os.path.join("/tmp", filename)
        file.save(file_path)

        # Load audio file and extract features
        try:
            # Load audio file with librosa
            audio, sr = librosa.load(file_path, sr=None)
            # Extract Mel Spectrogram features instead of MFCC
            mel_spectrogram = librosa.feature.melspectrogram(y=audio, sr=sr, n_mels=40)
            mel_spectrogram_db = librosa.power_to_db(mel_spectrogram, ref=np.max)
            mel_spectrogram_mean = np.mean(mel_spectrogram_db, axis=1)
            
            data = np.array(mel_spectrogram_mean).reshape(1, -1)

            # Make prediction
            prediction = model.predict(data)[0]
            confidence = max(model.predict_proba(data)[0])

            # Map prediction to label (Ensure your label mapping matches your training data)
            labels = {0: "gearbox", 1: "fan", 2: "pump", 3: "valve"}
            label = labels.get(prediction, "Unknown")

            # Return result
            return jsonify({
                "prediction": label,
                "confidence": float(confidence)
            })

        except Exception as e:
            return jsonify({"error": str(e)}), 500

    return jsonify({"error": "File type not allowed"}), 400

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)

