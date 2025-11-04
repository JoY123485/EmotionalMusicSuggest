-- 1. Users: 사용자 기본 정보
CREATE TABLE Users (
    user_id VARCHAR(255) PRIMARY KEY,      -- 고유 ID
    password VARCHAR(255) NOT NULL,             -- 비밀번호
    name VARCHAR(100) NOT NULL,                 -- 이름
    gender ENUM('0', '1', '2'),       -- 성별
    age INT,                                    -- 나이
    phone_number VARCHAR(20)
);

-- 2. UserSchedule: 사용자의 일정 정보 (근무/학업 등)
CREATE TABLE UserSchedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    day_of_week ENUM('Mon','Tue','Wed','Thu','Fri','Sat','Sun') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    sleep_time TIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 3. UserEmotionPreference: 감정-음악 선호 매핑
CREATE TABLE UserEmotionPreference (
    pref_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    emotion ENUM('Happy','Sad','Angry','Surprise') NOT NULL,
    music_mood ENUM('happy','sad','energetic','calm') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 4. Music: 음악 DB
CREATE TABLE Music (
    music_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    mood ENUM('happy','sad','energetic','calm') NOT NULL,
    url VARCHAR(500)
);
