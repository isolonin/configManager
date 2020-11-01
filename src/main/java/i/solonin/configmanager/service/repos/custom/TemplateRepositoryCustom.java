package i.solonin.configmanager.service.repos.custom;

import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.service.repos.AbstractRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepositoryCustom {
    boolean existsByName(Template template);
}
