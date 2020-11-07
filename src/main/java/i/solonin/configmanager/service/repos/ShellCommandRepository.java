package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.ShellCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShellCommandRepository extends JpaRepository<ShellCommand, Long>, AbstractRepository<ShellCommand> {
}
