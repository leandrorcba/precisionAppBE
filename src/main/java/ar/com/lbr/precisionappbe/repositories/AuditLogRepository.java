package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer>, JpaSpecificationExecutor<AuditLog> {
    
    @Query("SELECT DISTINCT a.usuario FROM AuditLog a ORDER BY a.usuario")
    List<String> findDistinctUsuarios();
}
