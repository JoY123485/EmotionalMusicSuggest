package com.example.demo.Repository;

import com.example.demo.DTO.UserScheduleDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserScheduleRepository {
	private final JdbcTemplate jdbcTemplate;

    public UserScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertSchedule(UserScheduleDTO dto) {
        String sql = """
            INSERT INTO UserSchedule (user_id, day_of_week, start_time, end_time, sleep_time)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, dto.getUser_id(), dto.getDay_of_week(),
                dto.getStart_time(), dto.getEnd_time(), dto.getSleep_time());
    }

    public List<UserScheduleDTO> findByUserId(String userId) {
        String sql = "SELECT * FROM UserSchedule WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserScheduleDTO.class), userId);
    }
}
