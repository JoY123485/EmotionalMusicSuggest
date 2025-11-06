package com.example.demo.DTO;

import java.sql.Time;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserScheduleDTO {
	private int	schedule_id;
	private String	user_id;
	private String	day_of_week;
	private Time	start_time;
	private Time	end_time;
	private Time	sleep_time;
}
