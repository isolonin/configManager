package i.solonin.configmanager.model;

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
                throw new UnsupportedOperationException("Данный тип оборудования пока не поддерживается");
        }
    }
}
