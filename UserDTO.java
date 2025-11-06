package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserDTO {
	private String	user_id;
	private String	password;
	private String	name;
	private int		gender;
	private int		age;
	private String	phone_number;
	
	private List<UserEmotionDTO> emotion;
	private List<UserScheduleDTO> schedules;
}
