package i.solonin.configmanager.model.template;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public abstract class Directive extends Line {
    private String text;
    private Pattern pattern;

    Directive(int line, String text) {
        super(line);
        this.text = text;
        this.pattern = Pattern.compile(text.replaceAll("\\$[^ ]*", "[^ ]*"));
    }

    public String getName() {
        return String.format("line %d: %s", getLine(), getText());
    }
}
