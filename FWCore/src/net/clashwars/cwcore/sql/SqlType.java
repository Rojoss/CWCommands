package net.clashwars.cwcore.sql;

public enum SqlType {
    TEXT(
            "text"),
    INTEGER(
            "int"),
    BIG_INTEGER(
            "bigint");

    private String name;

    private SqlType(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return name;
    }
}