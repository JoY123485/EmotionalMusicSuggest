import cv2
import face_recognition
import numpy as np
import pandas as pd
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

# 데이터 불러오기 및 병합
emotion_files = {
    "angry": r"emotionData\final_angry.csv",
    "happy": r"emotionData\final_happy.csv",
    "sad": r"emotionData\final_sad.csv",
    "surprise": r"emotionData\final_surprise.csv"
}

data = []
labels = []

for emotion, path in emotion_files.items():
    df = pd.read_csv(path)
    for px in df["pixels"]:
        img = np.array(px.split(), dtype="float32")
        if len(img) == 48*48:
            data.append(img)
            labels.append(emotion)

data = np.array(data)
labels = np.array(labels)

print("데이터셋 크기:", data.shape, "라벨 수:", len(labels))

# 학습 데이터 준비
X_train, X_test, y_train, y_test = train_test_split(data, labels, test_size=0.1, random_state=42)

# KNN 모델 학습
knn = KNeighborsClassifier(n_neighbors=5)
knn.fit(X_train, y_train)

y_pred = knn.predict(X_test)
print("테스트 정확도:", accuracy_score(y_test, y_pred))

# 실시간 웹캠 감정 예측
video = cv2.VideoCapture(0)
print("웹캠 얼굴 48x48 그레이스케일 + 감정 분석 시작 (q로 종료)\n")

while True:
    ret, frame = video.read()
    if not ret:
        break

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    face_locations = face_recognition.face_locations(rgb_frame, model="hog")

    for (top, right, bottom, left) in face_locations:
        face_image = rgb_frame[top:bottom, left:right]
        gray_face = cv2.cvtColor(face_image, cv2.COLOR_RGB2GRAY)
        resized_face = cv2.resize(gray_face, (48, 48))
        input_data = resized_face.flatten().reshape(1, -1)

        # 감정 예측
        emotion_pred = knn.predict(input_data)[0]

        # 얼굴 박스 + 감정 출력
        cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
        cv2.putText(frame, emotion_pred, (left, top - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 255), 2)

    cv2.imshow("Face Emotion (KNN)", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

video.release()
cv2.destroyAllWindows()
