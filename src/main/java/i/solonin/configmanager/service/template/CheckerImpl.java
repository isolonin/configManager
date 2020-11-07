package i.solonin.configmanager.service.template;

import i.solonin.configmanager.model.Template;
import i.solonin.configmanager.model.template.Command;
import i.solonin.configmanager.model.template.Directive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class CheckerImpl implements Checker {

    public void diff(Template template, List<String> deviceConfig) {
        try {
            List<Command> deviceCommands = getObject(deviceConfig);
            List<Command> templateCommands = getObject(template.getConfig());
            List<String> warnings = diff(templateCommands, deviceCommands);
            if (!warnings.isEmpty()) {
                log.warn("{}:", warnings);
                warnings.forEach(log::warn);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private List<Command> getObject(List<String> deviceConfig) {
        List<Command> result = new ArrayList<>();
        try {
            int line = 1;
            Command last = null;
            for (String string : deviceConfig) {
                Command command = new Command(line, string);
                if (string.matches("^[a-zA-Z].*") || string.matches("^\\s*sysname.*")) {
                    result.add(command);
                    last = command;
                    continue;
                }
                if (string.matches("^\\s[a-zA-Z].*")) {
                    if (last == null)
                        result.add(command);
                    else
                        last.getDirectives().add(command);
                }
                line++;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> diff(List<Command> templateCommands, List<Command> sourceCommands) {
        List<String> warnings = new ArrayList<>();
        List<Command> sources = new ArrayList<>(sourceCommands);

        templateCommands.forEach(tc -> {
            if (sources.stream().noneMatch(sc -> tc.getPattern().matcher(sc.getText()).find())) {
                warnings.add(tc.getName());
                return;
            }

            Iterator<Command> iterator = sources.iterator();
            while (iterator.hasNext()) {
                Command sc = iterator.next();
                if (tc.getPattern().matcher(sc.getText()).find()) {
                    List<Command> templateDirective = new ArrayList<>(tc.getDirectives());
                    List<Command> sourceDirective = new ArrayList<>(sc.getDirectives());

                    templateDirective.removeIf(td -> {
                        Iterator<Command> si = sourceDirective.iterator();
                        while (si.hasNext()) {
                            Directive sd = si.next();
                            if (td.getPattern().matcher(sd.getText()).find()) {
                                si.remove();
                                return true;
                            }
                        }
                        return false;
                    });
                    if (!templateDirective.isEmpty()) {
                        warnings.add(tc.getName());
                        templateDirective.forEach(td -> warnings.add("\t" + td.getName()));
                    }

                    iterator.remove();
                    break;
                }
            }
        });
        return warnings;
    }
}
