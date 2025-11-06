package com.example.demo.Repository;

import com.example.demo.DTO.MusicDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MusicRepository {
	 private final JdbcTemplate jdbcTemplate;

	    public MusicRepository(JdbcTemplate jdbcTemplate) {
	        this.jdbcTemplate = jdbcTemplate;
	    }

	    public List<MusicDTO> findByMood(String mood) {
	        String sql = "SELECT music_id AS id, title, artist, mood, url FROM Music WHERE mood = ?";
	        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MusicDTO.class), mood);
	    }
}
