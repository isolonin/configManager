package i.solonin.configmanager.model.template;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Command extends Directive {
    private List<Command> directives = new ArrayList<>();

    public Command(int line, String text) {
        super(line, text);
    }

    @Override
    public String toString() {
        return String.format("%s" + (directives.isEmpty() ? "" : ":\n\t%s"), getText(),
                String.join("\n\t", directives.stream().map(Directive::toString).toArray(String[]::new)));
    }
}
