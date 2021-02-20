package i.solonin.configmanager.service.template;

import i.solonin.configmanager.model.master.Template;
import i.solonin.configmanager.model.template.Divergence;

import java.util.List;

public interface Checker {
    List<Divergence> diff(Template template, List<String> deviceConfig);
}
