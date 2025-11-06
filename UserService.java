package com.example.demo.Service;

import org.springframework.stereotype.Service;

import com.example.demo.DTO.*;
import com.example.demo.Repository.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEmotionRepository emoRepository;
    private final UserScheduleRepository scheduleRepository;

    public UserService(UserRepository userRepository,
            UserEmotionRepository emoRepository,
            UserScheduleRepository scheduleRepository) {
    	this.userRepository = userRepository;
    	this.emoRepository = emoRepository;
    	this.scheduleRepository = scheduleRepository;
    }

    public void registerUser(UserDTO dto) {
    	userRepository.insertUser(dto);

    	dto.getEmotion().forEach(emo -> {
    		emo.setUser_id(dto.getUser_id());
    		emoRepository.insertEmotion(emo);
    	});

    	dto.getSchedules().forEach(schedule -> {
    		schedule.setUser_id(dto.getUser_id());
    		scheduleRepository.insertSchedule(schedule);
    	});
    }
}


