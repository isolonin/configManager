package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.DBEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;

@Service
@Slf4j
public abstract class AbstractController {
    void showInfoMessage(String message) {
        showMessage(message, FacesMessage.SEVERITY_INFO);
    }

    void showWarnMessage(String message) {
        showMessage(message, FacesMessage.SEVERITY_WARN);
    }

    void showErrorMessage(String message) {
        showMessage(message, FacesMessage.SEVERITY_ERROR);
    }

    void redirect(String path) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(path);
        } catch (Exception e) {
            log.error(e.getMessage());
            showErrorMessage(e.getMessage());
        }
    }

    private void showMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    <T extends DBEntity> void save(T entity, List<T> list, List<T> filtered, JpaRepository<T, Long> repository) {
        repository.save(entity);
        list.add(entity);
        if (filtered != null) filtered.add(entity);
    }

    <T extends DBEntity> void remove(T entity, List<T> list, List<T> filtered, JpaRepository<T, Long> repository) {
        try {
            list.remove(entity);
            if (filtered != null) filtered.remove(entity);
            repository.delete((T) entity);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
        }
    }
}
