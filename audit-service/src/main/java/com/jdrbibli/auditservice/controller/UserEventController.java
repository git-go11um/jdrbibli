package com.jdrbibli.auditservice.controller;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.service.UserEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class UserEventController {

    private final UserEventService service;

    public UserEventController(UserEventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserEvent> logEvent(
            @RequestParam Long userId,
            @RequestParam String eventType,
            @RequestParam(required = false) String details) {
        return ResponseEntity.ok(service.logEvent(userId, eventType, details));
    }

    @GetMapping
    public ResponseEntity<List<UserEvent>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }
}
