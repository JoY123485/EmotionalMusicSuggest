import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Flatten, Dense, Dropout

# -----------------------------
#  감정 데이터 불러오기
# -----------------------------
emotion_files = {
    "angry": r"emotionData\final_angry.csv",
    "happy": r"emotionData\final_happy.csv",
    "sad": r"emotionData\final_sad.csv",
    "surprise": r"emotionData\final_surprise.csv"
}

data = []
labels = []
label_map = {emotion: i for i, emotion in enumerate(emotion_files.keys())}

for emotion, path in emotion_files.items():
    df = pd.read_csv(path)
    for px in df["pixels"]:
        arr = np.array(px.split(), dtype="float32")
        if len(arr) == 48 * 48:
            data.append(arr)
            labels.append(label_map[emotion])

X = np.array(data).reshape(-1, 48, 48, 1) / 255.0
y = to_categorical(np.array(labels), num_classes=len(emotion_files))

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.1, random_state=42)
print("✅ 데이터 로드 완료:", X_train.shape, y_train.shape)

# -----------------------------
#  CNN 모델 정의
# -----------------------------
model = Sequential([
    Conv2D(32, (3, 3), activation='relu', input_shape=(48, 48, 1)),
    MaxPooling2D(2, 2),

    Conv2D(64, (3, 3), activation='relu'),
    MaxPooling2D(2, 2),

    Flatten(),
    Dense(128, activation='relu'),
    Dropout(0.5),
    Dense(len(emotion_files), activation='softmax')
])

model.compile(optimizer='adam',
              loss='categorical_crossentropy',
              metrics=['accuracy'])

model.summary()

# -----------------------------
#  모델 학습 및 저장
# -----------------------------
print("\n🧠 모델 학습 중...")
history = model.fit(
    X_train, y_train,
    validation_data=(X_test, y_test),
    epochs=30,
    batch_size=64
)

model.save("emotion_model.h5")
print("\n✅ 모델 저장 완료: emotion_model.h5 생성됨")

# -----------------------------
#  성능 평가
# -----------------------------
loss, acc = model.evaluate(X_test, y_test)
print(f"테스트 정확도: {acc:.3f}")