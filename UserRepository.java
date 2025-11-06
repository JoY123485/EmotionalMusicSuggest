package com.example.demo.Repository;

import com.example.demo.DTO.UserDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
	private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertUser(UserDTO user) {
        String sql = """
            INSERT INTO Users (user_id, password, name, gender, age, phone_number)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                user.getUser_id(),
                user.getPassword(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getPhone_number());
    }
}
