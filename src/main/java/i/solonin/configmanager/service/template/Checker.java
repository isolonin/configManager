package i.solonin.configmanager.service.template;

import i.solonin.configmanager.model.template.Command;

import java.util.List;

public interface Checker {
    List<String> diff(List<Command> templateCommands, List<Command> sourceCommands);
}
