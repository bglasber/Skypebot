package skypebot.db.schema;

import skypebot.db.ISqlString;

public class SchemaConstructorString implements ISqlString {

    private SchemaConstructorType type;
    private String string;

    public SchemaConstructorString(
        String s,
        SchemaConstructorType type
    ) {
        string = s;
        this.type = type;

    }

    public String toString() {
        return string;
    }

    public SchemaConstructorType getType() {
        return type;

    }


}
