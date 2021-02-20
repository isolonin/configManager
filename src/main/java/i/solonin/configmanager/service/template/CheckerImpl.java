package i.solonin.configmanager.service.template;

import i.solonin.configmanager.model.master.Template;
import i.solonin.configmanager.model.template.Command;
import i.solonin.configmanager.model.template.Directive;
import i.solonin.configmanager.model.template.Divergence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class CheckerImpl implements Checker {
    private final List<String> commands = new ArrayList<>();

    @PostConstruct
    public void init() {
        try (Stream<String> stream = Files.lines(Paths.get("config/commands.cfg"))) {
            stream.filter(s -> !StringUtils.isEmpty(s)).forEach(commands::add);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Divergence> diff(Template template, List<String> deviceConfig) {
        List<Divergence> result = new ArrayList<>();
        try {
            List<Command> deviceCommands = getObject(deviceConfig);
            List<Command> templateCommands = getObject(template.getConfigByLines());
            result = diff(templateCommands, deviceCommands);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    private List<Command> getObject(List<String> deviceConfig) {
        List<Command> result = new ArrayList<>();
        try {
            int line = 1;
            Command last = null;
            for (String string : deviceConfig) {
                Command command = new Command(line, string);
                if (string.matches("^#.*")) {
                    last = null;

                    line++;
                    continue;
                }
                if (string.matches("^[a-zA-Z].*")) {
                    result.add(command);
                    last = command;

                    line++;
                    continue;
                }
                if (commands.contains(string.replaceFirst("^\\s*([^\\s]*) .*", "$1"))) {
                    result.add(command);

                    line++;
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

    private List<Divergence> diff(List<Command> templateCommands, List<Command> sourceCommands) {
        List<Divergence> result = new ArrayList<>();
        List<Command> sources = new ArrayList<>(sourceCommands);

        templateCommands.forEach(tc -> {
            Command sourceCommand = sources.stream().filter(sc -> tc.getPattern().matcher(sc.getText()).find()).findFirst().orElse(null);
            if (sourceCommand == null) {
                result.add(new Divergence(tc.getLine(), tc.getText()));
                return;
            }

            sources.remove(sourceCommand);

            List<Command> templateDirective = new ArrayList<>(tc.getDirectives());
            List<Command> sourceDirective = new ArrayList<>(sourceCommand.getDirectives());

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
                result.add(new Divergence(tc.getLine(), tc.getText()));
                templateDirective.forEach(td -> result.add(new Divergence(td.getLine(), td.getText(), true)));
            }
        });
        return result;
    }
}
