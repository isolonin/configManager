package i.solonin.configmanager.service.template;

import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.model.template.Command;

import java.util.List;

public interface Checker {
    void diff(Template template, List<String> deviceConfig);

    List<String> diff(List<Command> templateCommands, List<Command> sourceCommands);
}
