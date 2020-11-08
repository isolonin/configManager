package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.CheckingResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckingResultRepository extends JpaRepository<CheckingResult, Long> {
}
