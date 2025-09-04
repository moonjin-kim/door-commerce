package com.loopers.infrastructure.audit_log;

import com.loopers.domain.audit_log.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogJpaRepository extends JpaRepository<AuditLog, Long> {
}
