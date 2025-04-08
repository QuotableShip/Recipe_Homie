import chardet

def detect_file_encoding(file_path):
    with open(file_path, 'rb') as f:
        raw_data = f.read()
        result = chardet.detect(raw_data)
        encoding = result['encoding']
        confidence = result['confidence']
        return encoding, confidence

file_path = r"insert_directory_here"
encoding, confidence = detect_file_encoding(file_path)
print(f"Detected encoding: {encoding} with confidence: {confidence}")

