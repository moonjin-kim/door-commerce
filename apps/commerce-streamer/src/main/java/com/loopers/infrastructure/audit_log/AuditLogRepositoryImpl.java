package com.loopers.infrastructure.audit_log;

import com.loopers.domain.audit_log.AuditLog;
import com.loopers.domain.audit_log.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {
    private final AuditLogJpaRepository auditLogJpaRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return auditLogJpaRepository.save(auditLog);
    }
}
