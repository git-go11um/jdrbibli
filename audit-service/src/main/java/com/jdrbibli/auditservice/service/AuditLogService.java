package com.jdrbibli.auditservice.service;

import com.jdrbibli.auditservice.entity.AuditLog;
import com.jdrbibli.auditservice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog save(AuditLog log) {
        return repository.save(log);
    }

    public List<AuditLog> findAll() {
        return repository.findAll();
    }
}
