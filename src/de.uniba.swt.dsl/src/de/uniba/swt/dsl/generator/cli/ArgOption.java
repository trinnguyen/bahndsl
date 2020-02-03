package de.uniba.swt.dsl.generator.cli;

public class ArgOption {
    private String name;
    private String desc;
    private boolean hasValue;
    private String paramName;

    public ArgOption(String name, String desc) {
        this(name, desc, false, null);
    }

    public ArgOption(String name, String desc, boolean hasValue, String paramName) {
        this.name = name;
        this.desc = desc;
        this.hasValue = hasValue;
        this.paramName = paramName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getFormattedName() {
        return "-" + name;
    }

    public String getFormattedKeyValue() {
        var builder = new StringBuilder().append(getFormattedName());
        if (hasValue) {
            builder.append(" ").append("<").append(paramName).append(">");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getFormattedKeyValue();
    }
}
