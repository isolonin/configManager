package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.ShellCommand;
import i.solonin.configmanager.service.repos.ShellCommandRepository;
import i.solonin.configmanager.service.repos.custom.AbstractRepositoryCustom;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;

@Getter
@Slf4j
@ViewScoped
@Named("shellCommandsController")
public class ShellCommandsController extends AbstractController {
    @Autowired
    private ShellCommandRepository shellCommandRepository;
    @Qualifier("shellCommandRepositoryCustomImpl")
    @Autowired
    private AbstractRepositoryCustom shellCommandRepositoryCustom;

    private ShellCommand shellCommand = new ShellCommand();
    @Setter
    private List<ShellCommand> filteredShellCommands;
    private List<ShellCommand> shellCommands;

    public void init() {
        shellCommands = shellCommandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void save() {
        try {
            save(shellCommand);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private void save(ShellCommand command) {
        if (shellCommandRepositoryCustom.existsByName(command)) {
            showWarnMessage("Шаблон с таким именем уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
        save(command, shellCommands, filteredShellCommands, shellCommandRepository);
        this.shellCommand = new ShellCommand();
    }

    public void onRowEdit(RowEditEvent event) {
        ShellCommand command = (ShellCommand) event.getObject();
        save(command);
    }

    public void remove(ShellCommand command) {
        remove(command, shellCommands, filteredShellCommands, shellCommandRepository);
    }
}
