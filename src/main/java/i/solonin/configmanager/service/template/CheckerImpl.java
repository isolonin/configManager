package i.solonin.configmanager.service.template;

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

//    public void diff(String template, List<String> sources) throws Exception {
//        Path templatePath = Paths.get(template);
//        if (!Files.exists(templatePath))
//            throw new Exception("Template file not existed");
//        if (!Files.isRegularFile(templatePath))
//            throw new Exception(template + " is not regular file");
//        List<File> sourceFiles = getSources(sources, template);
//        if (sourceFiles.isEmpty())
//            throw new Exception("Source files not found");
//
//        List<Command> templateCommands = getObject(templatePath);
//        sourceFiles.forEach(f -> {
//            String absolutePath = f.getAbsolutePath();
//            List<Command> sourceCommands = getObject(Paths.get(absolutePath));
//            List<String> warnings = diff(templateCommands, sourceCommands);
//            if (!warnings.isEmpty()) {
//                log.warn("{}:", absolutePath);
//                warnings.forEach(log::warn);
//            }
//        });
//    }

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
