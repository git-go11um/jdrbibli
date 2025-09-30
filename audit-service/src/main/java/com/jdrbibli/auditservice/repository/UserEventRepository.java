package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEventRepository extends MongoRepository<UserEvent, String> {
}
