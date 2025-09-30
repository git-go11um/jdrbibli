package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.UserEvent;
import com.jdrbibli.auditservice.repository.UserEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserEventService {

    private final UserEventRepository repository;

    public UserEventService(UserEventRepository repository) {
        this.repository = repository;
    }

    public UserEvent logEvent(Long userId, String eventType, String details) {
        UserEvent event = new UserEvent(
                userId,
                eventType,
                Instant.now(),
                details
        );
        return repository.save(event);
    }

    public List<UserEvent> getAllEvents() {
        return repository.findAll();
    }
}
