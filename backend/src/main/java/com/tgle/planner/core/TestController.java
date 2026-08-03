package com.tgle.planner.core;

import com.tgle.planner.core.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Void>> sayHello() {
        return ResponseEntity.ok(ApiResponse.of("HELLO"));
    }
}
