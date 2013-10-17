package skypebot.db.schema;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Schema {


    public static class Tables {

        public static Table ResponseTable =
            new Table(
                "responses",
                new String[]{ "query", "response" },
                "responsesIndex",
                "query",
                TableType.RESPONSE
            );

        public static Table ItemTable =
            new Table(
                "items",
                new String[]{ "item" },
                "itemIndex",
                "item",
                TableType.ITEMS
            );

        public static Table NounTable =
            new Table(
                "noun",
                new String[]{ "noun" },
                "nounIndex",
                "noun",
                TableType.NOUN
            );

        public static Table VideoTable =
            new Table(
                "videos",
                new String[]{ "username", "url" },
                "videosIndex",
                "username",
                TableType.VIDEOS
            );

        public static Table VerbTable =
            new Table(
                "verb",
                new String[]{ "verb" },
                "verbIndex",
                "verb",
                TableType.VERB
            );

        public static Table VerbedTable =
            new Table(
                "verbed",
                new String[]{ "verbed" },
                "verbedIndex",
                "verbed",
                TableType.VERBED
            );

        public static Table VerbingTable =
            new Table(
                "verbing",
                new String[]{ "verbing" },
                "verbingIndex",
                "verbing",
                TableType.VERBING
            );

        public static Table AdjectiveTable =
            new Table(
                "adjective",
                new String[]{ "adjective" },
                "adjectiveIndex",
                "adjective",
                TableType.ADJECTIVE
            );

        public static Table AliasTable =
            new Table(
                "alias",
                new String[]{ "alias", "realId" },
                "aliasIndex",
                "alias",
                TableType.ALIAS
            );
    }

    public Table getResponseTable() {
        return Tables.ResponseTable;
    }

    public Table getItemTable() {
        return Tables.ItemTable;
    }

    public Table getNounTable() {
        return Tables.NounTable;
    }

    public Table getVideosTable() {
        return Tables.VideoTable;
    }

    public Table getVerbTable() {
        return Tables.VerbTable;
    }

    public Table getVerbedTable() {
        return Tables.VerbedTable;
    }

    public Table getVerbingTable() {
        return Tables.VerbingTable;
    }

    public Table getAdjectiveTable() {
        return Tables.AdjectiveTable;
    }

    public Table getAliasTable() {
        return Tables.AliasTable;
    }

    public List<SchemaConstructorString> getSchemaConstructionStrings() {

        Field[] fields = Schema.Tables.class.getFields();
        List<SchemaConstructorString> schemaStrings = new ArrayList<SchemaConstructorString>();
        for( Field f : fields ) {
            Table t = null;
            try {
                t = ( Table ) f.get( t );
                String constructTableString = "CREATE TABLE IF NOT EXISTS " + t.getTableName() + " ( ";
                for( String tableField : t.getTableFields() ) {
                    constructTableString += tableField + " TEXT, ";
                }
                //Get rid of the ', ' on the end
                constructTableString = constructTableString.substring(
                    0,
                    constructTableString.length() - 2
                );
                constructTableString += " );";
                schemaStrings.add(
                    new SchemaConstructorString(
                        constructTableString,
                        SchemaConstructorType.TABLECONSTRUCTOR
                    )
                );
                String constructIndexString = "CREATE INDEX IF NOT EXISTS " + t.getTableIndex() + " ON " + t.getTableName() + " ( ";
                constructIndexString += t.getIndexField() + " );";
                schemaStrings.add(
                    new SchemaConstructorString(
                        constructIndexString,
                        SchemaConstructorType.INDEXCONSTRUCTOR
                    )
                );
            } catch( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }
        return schemaStrings;
    }

}
