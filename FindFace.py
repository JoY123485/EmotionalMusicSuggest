import cv2
import face_recognition
import numpy as np

# 웹캠 열기
video = cv2.VideoCapture(0)
print("얼굴 인식 시작 (q로 종료)\n")

while True:
    ret, frame = video.read()
    if not ret:
        break

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # 얼굴 감지
    face_locations = face_recognition.face_locations(rgb_frame, model="hog")

    for (top, right, bottom, left) in face_locations:
        # 얼굴 영역 crop
        face_image = rgb_frame[top:bottom, left:right]
        # 랜드마크 추출
        landmarks_list = face_recognition.face_landmarks(face_image)

        # 랜드마크 표시
        for landmarks in landmarks_list:
            for feature, points in landmarks.items():
                for (x, y) in points:
                    cv2.circle(frame, (left + x, top + y), 2, (0, 255, 255), -1)

        # 48x48 그레이스케일
        gray_face = cv2.cvtColor(face_image, cv2.COLOR_RGB2GRAY)
        face_resized = cv2.resize(gray_face, (48, 48))
        cnn_input = np.expand_dims(face_resized, axis=(0, -1))  # shape: (1,48,48,1)

        # 얼굴 박스 표시
        cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)

    cv2.imshow("Face 48x48 Gray + Landmarks", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

video.release()
cv2.destroyAllWindows()

