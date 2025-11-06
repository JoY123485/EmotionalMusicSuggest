import cv2
import requests
import numpy as np
from keras.models import load_model

# 감정 인식 모델 로드
model = load_model("emotion_model.h5")
emotion_labels = ['Happy', 'Sad', 'Angry', 'Surprise']

# 얼굴 인식
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")

# Spring Boot 서버 주소
SERVER_URL = "http://localhost:8080/api/recommend"


def predict_emotion(frame):
    """얼굴에서 감정 예측"""
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)

    for (x, y, w, h) in faces:
        # 얼굴 영역 추출
        face = gray[y:y+h, x:x+w]
        face = cv2.resize(face, (48, 48)) / 255.0
        face = np.expand_dims(face, axis=(0, -1))  # 모델 입력 형태 (1, 48, 48, 1)

        # 모델 예측
        prediction = model.predict(face)
        emotion = emotion_labels[np.argmax(prediction)]

        # 얼굴 박스 + 감정 표시
        cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
        cv2.putText(frame, emotion, (x, y-10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.9, (36, 255, 12), 2)

        return emotion  # 첫 번째 얼굴만 반환

    return None


def get_music_recommendation(user_id, emotion):
    """Spring Boot 서버로 감정 전달 후 추천 음악 받아오기"""
    url = f"{SERVER_URL}/{user_id}/{emotion}"
    try:
        response = requests.get(url, timeout=5)

        if response.status_code == 200:
            music_list = response.json()
            print(f"\n🎧 추천 결과 ({emotion}):")

            for music in music_list:
                print(f" - {music['title']} by {music['artist']} ({music['mood']})")

            return music_list

        else:
            print(f" 서버 오류: {response.status_code}")

    except requests.exceptions.RequestException as e:
        print(f" 서버 연결 실패: {e}")

    return []


def main():
    """웹캠을 통해 실시간 감정 인식 및 음악 추천"""
    cap = cv2.VideoCapture(0)
    user_id = "yuna"  # 로그인된 사용자 ID

    print(" 얼굴 인식 및 감정 분석 시작 (종료하려면 Q를 누르세요)")

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        emotion = predict_emotion(frame)

        # 감정이 감지되면 서버에 전송
        if emotion:
            music_list = get_music_recommendation(user_id, emotion)

        # 감정 및 프레임 표시
        cv2.imshow("Emotion Recognition", frame)

        # q 누르면 종료
        if cv2.waitKey(3000) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
