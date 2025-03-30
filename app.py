from flask import Flask, request, jsonify
import pickle
import numpy as np
import os
from werkzeug.utils import secure_filename
from pyAudioAnalysis import audioTrainTest as aT
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
            # Extract 10 MFCC features instead of the default 20
            feature_vector = librosa.feature.mfcc(y=audio, sr=sr, n_mfcc=10).mean(axis=1)
            data = np.array(feature_vector).reshape(1, -1)

            # Make prediction
            prediction = model.predict(data)[0]
            confidence = max(model.predict_proba(data)[0])

            # Map prediction to label
            labels = {0: "fan", 1: "gearbox", 2: "pump", 3: "valve"}
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

