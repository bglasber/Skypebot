package skypebot.db;

import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.schema.Schema;
import skypebot.db.schema.SchemaConstructorString;
import skypebot.db.schema.SchemaConstructorType;
import skypebot.db.schema.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager implements IDbManager {

    private IDbProvider provider;
    private Schema schema;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );
    private static String[] prevResult = new String[ 2 ];

    public DbManager( Schema s ) {
        schema = s;
    }

    public void setProvider( IDbProvider provider ) {
        this.provider = provider;
    }

    public void constructSchema() throws SQLException {
        for( SchemaConstructorString schemaConstructor : schema.getSchemaConstructionStrings() ) {
            if( schemaConstructor.getType() == SchemaConstructorType.TABLECONSTRUCTOR ) {
                provider.createTable( schemaConstructor );
            }
            else if( schemaConstructor.getType() == SchemaConstructorType.INDEXCONSTRUCTOR ) {
                provider.createIndex( schemaConstructor );

            }
        }
    }

    public Schema getSchema() {
        return schema;
    }

    public String getSingleFromDbThatContains(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException {

        ResultSet resultSet = provider.getResultsByContains(
            table.getTableName(),
            table.getTableFields(),
            fieldNameToCheck,
            messageToMatch
        );

        List<String> resultList = createResultSet(
            fieldNameToReturn,
            resultSet
        );
        logger.trace( "Got results from db query: " );
        logger.trace( resultList );
        return getRandomResult(
            resultList
        );
    }

    public String getSingleFromDbThatEquals(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException {

        ResultSet resultSet = provider.getResultsByEquality(
            table.getTableName(),
            table.getTableFields(),
            fieldNameToCheck,
            messageToMatch
        );

        List<String> resultList = createResultSet(
            fieldNameToReturn,
            resultSet
        );
        logger.trace( "Got results from db query: " );
        logger.trace( resultList );
        return getRandomResult(
            resultList
        );
    }

    private List<String> createResultSet(
        String fieldNameToReturn,
        ResultSet resultSet
    ) throws SQLException {
        List<String> resultList = new ArrayList<String>();
        while( resultSet.next() ) {
            resultList.add( resultSet.getString( fieldNameToReturn ) );
        }
        return resultList;
    }

    public String getSingleFromDb(
        Table table,
        String fieldNameToReturn
    ) throws SQLException {
        ResultSet resultSet = provider.getResultQuery(
            table.getTableName(),
            new String[]{ fieldNameToReturn }
        );
        List<String> resultList = createResultSet(
            fieldNameToReturn,
            resultSet
        );
        return getRandomResult(
            resultList
        );
    }

    private String getRandomResult(
        List<String> resultList
    ) {
        try {
            String result = resultList.get(
                ( int ) ( Math.random() * ( resultList.size() ) )
            );
            logger.debug( "Got random result: " + result );
            return result;
        } catch( IndexOutOfBoundsException e ) {
            //No valid responses.
            return null;
        }
    }


    public boolean insertFieldsIntoTable(
        Table table,
        String[] fieldsToInsert
    ) {
        String tableName = table.getTableName();
        try {
            provider.insertInto(
                tableName,
                fieldsToInsert
            );
            logger.debug( "Successfully inserted fields into: " + tableName );
            return true;
        } catch( SQLException e ) {
            logger.error( "Failed to insert fields into table: " + tableName );
            logger.error( e );
            return false;
        }

    }

    public long getDbCount(
        Table table
    ) {
        try {
            return provider.getNumberOfEntries( table.getTableName() );
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
            return 0;
        }
    }

    public boolean deleteRowFromTable(
        Table table,
        String[] fieldsToCheck,
        String[] fieldValuesExpected
    ) {
        try {
            return provider.deleteRowFromTable(
                table.getTableName(),
                fieldsToCheck,
                fieldValuesExpected
            );
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
            return false;
        }
    }

    public String deleteRandomRowFromTable(
        Table table,
        String fieldToCheck,
        String fieldToReturn
    ) {

        String[] fieldsToReturn = getFieldsToReturn(
            fieldToCheck,
            fieldToReturn
        );
        try {
            ResultSet results = provider.getResultQuery(
                table.getTableName(),
                fieldsToReturn
            );
            Map<String, String> stringMap = createFieldMap(
                fieldToCheck,
                fieldToReturn,
                results
            );

            List<String> valuesAsList = new ArrayList<String>( stringMap.keySet() );
            String randomKey = getRandomResult( valuesAsList );
            String randomValue = stringMap.get( randomKey );
            provider.deleteRowFromTable(
                table.getTableName(),
                new String[]{ fieldToCheck },
                new String[]{ randomKey }
            );
            return randomValue;
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
            return null;
        }

    }

    @Override
    public List<String> getEntireTable(
        Table table,
        String fieldToReturn
    ) {
        try {
            ResultSet results = provider.getEntireTable(
                table.getTableName(),
                fieldToReturn
            );
            return createResultSet(
                fieldToReturn,
                results
            );
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        }
        return new ArrayList<String>();
    }

    private Map<String, String> createFieldMap(
        String fieldToCheck,
        String fieldToReturn,
        ResultSet results
    ) throws SQLException {
        Map<String, String> stringMap = new HashMap<String, String>();
        while( results.next() ) {
            String key = results.getString( fieldToCheck );
            if( !fieldToCheck.equals( fieldToReturn ) ) {
                String val = results.getString( fieldToReturn );
                stringMap.put(
                    key,
                    val
                );
            }
            else {
                stringMap.put(
                    key,
                    key
                );
            }

        }
        return stringMap;
    }

    private String[] getFieldsToReturn(
        String fieldToCheck,
        String fieldToReturn
    ) {
        String[] fieldsToGet;
        if( fieldToReturn.equals( fieldToCheck ) ) {
            fieldsToGet = new String[]{
                fieldToReturn,
            };
        }
        else {
            fieldsToGet = new String[]{
                fieldToCheck,
                fieldToReturn
            };
        }
        return fieldsToGet;
    }
}
