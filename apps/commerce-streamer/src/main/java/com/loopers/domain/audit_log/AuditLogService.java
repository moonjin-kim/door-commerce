package com.loopers.domain.audit_log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLog createAuditLog(AuditLogCommand.Save command) {
        AuditLog auditLog = AuditLog.create(command);

        return auditLogRepository.save(auditLog);
    }
}
