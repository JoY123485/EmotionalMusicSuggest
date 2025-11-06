package com.example.demo.Service;

import com.example.demo.DTO.MusicDTO;
import com.example.demo.DTO.UserEmotionDTO;
import com.example.demo.DTO.UserScheduleDTO;
import com.example.demo.Repository.MusicRepository;
import com.example.demo.Repository.UserEmotionRepository;
import com.example.demo.Repository.UserScheduleRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

//추천 서비스-> 선호분위기, 현재 시간 보정, 해당 감정의 목록 반환
@Service
public class SuggestionService {
	private final MusicRepository musicRepository;
    private final UserEmotionRepository emoRepository;
    private final UserScheduleRepository scheduleRepository;
    
    public SuggestionService(MusicRepository musicRepository,
    		UserEmotionRepository emoRepository,
    		UserScheduleRepository scheduleRepository) {
    	this.musicRepository = musicRepository;
    	this.emoRepository = emoRepository;
    	this.scheduleRepository = scheduleRepository;
    }
    
    public List<MusicDTO> suggested(String user_id, String emotion) {
        //감정별 선호 분위기 가져옴
    	UserEmotionDTO emo = emoRepository.findByUserIdAndEmotion(user_id, emotion);
        String baseMood = (emo != null) ? emo.getMusic_mood() : "calm";
        
        //가중치 초기화
        Map<String, Double> weights = new HashMap<>();
        weights.put("calm", 0.0);
        weights.put("energetic", 0.0);
        weights.put("sad", 0.0);
        weights.put("happy", 0.0);

        // 기본 감정 mood에 가중치 부여        
        weights.put(baseMood, weights.get(baseMood) + 0.7);
        
        //현재 시간
        LocalTime now = LocalTime.now();
        String today = LocalDate.now().getDayOfWeek().name().substring(0, 3);

        // 스케줄 가져오기
        List<UserScheduleDTO> schedules = scheduleRepository.findByUserId(user_id);
        UserScheduleDTO todaySchedule = schedules.stream()
        		.filter(s -> s.getDay_of_week().equalsIgnoreCase(today))
                .findFirst()
                .orElse(null);

        if (todaySchedule != null) {
        	LocalTime start = toLocal(todaySchedule.getStart_time());
            LocalTime end = toLocal(todaySchedule.getEnd_time());
            LocalTime sleep = toLocal(todaySchedule.getSleep_time());

            // 현재시간<->주요 시간 거리 계산
            double toWorkStart = Math.abs(ChronoUnit.MINUTES.between(now, start));
            double toWorkEnd = Math.abs(ChronoUnit.MINUTES.between(now, end));
            double toSleep = Math.abs(ChronoUnit.MINUTES.between(now, sleep));

            // 출퇴근시간엔 energetic 비중 증가
            if (toWorkStart < 60)
                weights.put("energetic", weights.get("energetic") + 0.3 * (1 - toWorkStart / 60.0));
            if (toWorkEnd < 60)
                weights.put("energetic", weights.get("energetic") + 0.3 * (1 - toWorkEnd / 60.0));

            // 수면 90분 전이면 calm 비중 증가
            if (toSleep < 90)
                weights.put("calm", weights.get("calm") + 0.4 * (1 - toSleep / 90.0));

            // 근무/수업 중이면 집중용 calm 약간 증가
            if (now.isAfter(start) && now.isBefore(end))
                weights.put("calm", weights.get("calm") + 0.3);
        }

		// 최종 mood 결정
        String finalMood = weights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        System.out.println("Weighted Mood Scores: " + weights);
        System.out.println("Final mood: " + finalMood);

        
        return musicRepository.findByMood(finalMood);
	}
    
    private LocalTime toLocal(Time t) {
    	return (t != null) ? t.toLocalTime() : LocalTime.MIDNIGHT;
    }
}
