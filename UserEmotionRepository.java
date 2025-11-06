package com.example.demo.Repository;

import com.example.demo.DTO.UserEmotionDTO;
import com.example.demo.DTO.UserScheduleDTO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserEmotionRepository {
	private final JdbcTemplate jdbcTemplate;

    public UserEmotionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void insertEmotion(UserEmotionDTO dto) {
        String sql = """
            INSERT INTO UserEmotion (user_id, emotion, music_mood)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, dto.getUser_id(), dto.getEmotion(),
                dto.getMusic_mood());
    }

    public UserEmotionDTO findByUserIdAndEmotion(String userId, String emotion) {
        String sql = "SELECT * FROM UserEmotion WHERE user_id = ? AND emotion = ?";
        try {
            return jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(UserEmotionDTO.class),
                userId, emotion
            );
        } catch (Exception e) {
            return null;
        }
    }

    public List<UserEmotionDTO> findByUserId(String userId) {
        String sql = "SELECT * FROM UserEmotion WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserEmotionDTO.class), userId);
    }
}
