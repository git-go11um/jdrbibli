package com.jdrbibli.auditservice.repository;

import com.jdrbibli.auditservice.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
