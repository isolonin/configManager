package i.solonin.configmanager.model.master;

import static i.solonin.configmanager.constant.Constants.UNSUPPORTED_TYPE;

public enum EquipmentType {
    HUAWEI("Huawei"),
    DLINK("D-Link");

    private final String name;

    EquipmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return String.format("%s (%s)", name, getCommand());
    }

    public String getCommand() {
        switch (this) {
            case HUAWEI:
                return "show conf curent";
            case DLINK:
                return "display current";
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_TYPE);
        }
    }

    public String getLoginMatchers() {
        switch (this) {
            case HUAWEI:
            case DLINK:
                return "login:";
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_TYPE);
        }
    }

    public String getPasswordMatchers() {
        switch (this) {
            case HUAWEI:
            case DLINK:
                return "Password:";
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_TYPE);
        }
    }

    public String getPromptMatchers() {
        switch (this) {
            case HUAWEI:
            case DLINK:
                return "#";
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_TYPE);
        }
    }
}
