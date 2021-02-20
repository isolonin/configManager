package i.solonin.configmanager.model.check;

import lombok.Getter;

public enum ResultType {
    EXECUTING("Выполняется"),
    CONNECTION_FAILED("Не удалось подключиться"),
    LOGIN_FAILED("Не удалось авторизоваться"),
    COMMAND_FAILED("Не удалось выполнить команду"),
    MATCHES_PATTERN("Соответствует шаблону"),
    NOT_MATCHES_PATTERN("Не соответствует шаблону"),
    COMPLETED("Выполнено");

    @Getter
    private final String name;

    ResultType(String name) {
        this.name = name;
    }
}
