package skypebot.tests;

import com.skype.Chat;
import com.sun.corba.se.impl.orb.ParserTable;
import skypebot.variables.VariableExpander;
import skypebot.tests.mock.MockDbManager;

import java.lang.AssertionError;
import java.lang.NullPointerException;
import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;
import org.junit.*;
import org.junit.Assert;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 4:59 PM
 */
public class VariableExpanderTest {

    private VariableExpander expander;

    @Before
    public void CreateVariableExpander(){
        try {
           expander = new VariableExpander( new MockDbManager() );
        }
        catch( IllegalAccessException e ){

        }
        catch( InstantiationException e){

        }
        catch( NoSuchMethodException e){

        }
        catch( InvocationTargetException e){

        }
    }
    @Test
    public void TestSingleNounExpansion(){
        Assert.assertEquals(
            "nounForTest",
            expander.expandVariables(
                "testName",
                null,
                "$noun"
            )
        );
    }

    @Test
    public void TestMultipleNounExpansion(){
        Assert.assertEquals(
            "nounForTest dontChangeMe nounForTest",
            expander.expandVariables(
                "testName",
                null,
                "$noun dontChangeMe $noun"
            )
        );
    }

    @Test
    public void TestWhoExpansion(){
        Assert.assertEquals(
            "I am testName",
            expander.expandVariables(
                "testName",
                null,
                "I am $who"
            )
        );
    }

    //We don't have a way to get a stub chat object,
    //so we will a pointer exception. Verifies that we at least get that far.
    @Test(expected=NullPointerException.class)
    public void TestSomeoneExpansion(){
        Assert.assertEquals(
            "Someone is: someone",
            expander.expandVariables(
                "testName",
                null,
                "Someone is: $someone"
            )
        );
    }
}
