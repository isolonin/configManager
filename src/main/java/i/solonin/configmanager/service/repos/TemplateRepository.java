package i.solonin.configmanager.service.repos;

import i.solonin.configmanager.model.master.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long>, AbstractRepository<Template> {
}
