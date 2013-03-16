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
import java.util.List;

public class DbManager implements IDbManager {

    private IDbProvider provider;
    private Schema schema;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

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

        List<String> resultList = CreateResultSet(
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

        List<String> resultList = CreateResultSet(
            fieldNameToReturn,
            resultSet
        );
        logger.trace( "Got results from db query: " );
        logger.trace( resultList );
        return getRandomResult(
            resultList
        );
    }

    private List<String> CreateResultSet(
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
        List<String> resultList = CreateResultSet(
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
}
