package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.check.ExecutionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionResultRepository extends JpaRepository<ExecutionResult, Long> {
}
