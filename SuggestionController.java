package com.example.demo.Controller;

import com.example.demo.DTO.MusicDTO;
import com.example.demo.Service.SuggestionService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
public class SuggestionController {
	 private final SuggestionService suggestionService;

	    public SuggestionController(SuggestionService suggestionService) {
	        this.suggestionService = suggestionService;
	    }

	    @GetMapping("/{user_id}/{emotion}")
	    public List<MusicDTO> suggested(@PathVariable String user_id, @PathVariable String emotion) {
	        return suggestionService.suggested(user_id, emotion);
	    }
}
