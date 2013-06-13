package net.clashwars.cwcore.sql;

public class SqlValue {
    private SqlType type;
    private String  value;
    private boolean notnull        = true;
    private boolean auto_increment = false;

    public SqlValue(String value, SqlType type) {
        this.value = value;
        this.type = type;
    }

    public SqlValue(String value, SqlType type, boolean notnull) {
        this.value = value;
        this.type = type;
        this.notnull = notnull;
    }

    public SqlValue(String value, SqlType type, boolean notnull, boolean auto_increment) {
        this.value = value;
        this.type = type;
        this.notnull = notnull;
        this.auto_increment = auto_increment;
    }

    public SqlType getType() {
        return type;
    }

    public void setType(SqlType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNotNull() {
        return notnull;
    }

    public void setNotNull(boolean notnull) {
        this.notnull = notnull;
    }

    public boolean isAutoIncrement() {
        return auto_increment;
    }

    public void setAutoIncrement(boolean auto_increment) {
        this.auto_increment = auto_increment;
    }
}