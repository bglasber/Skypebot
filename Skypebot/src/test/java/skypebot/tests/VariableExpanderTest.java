package skypebot.tests;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.SkypeException;
import com.skype.User;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import skypebot.tests.mock.MockDbManager;
import skypebot.variables.VariableExpander;
import org.junit.*;

import java.lang.reflect.InvocationTargetException;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 4:59 PM
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { Chat.class, User.class } )
public class VariableExpanderTest {

    private VariableExpander expander;

    @Before
    public void CreateVariableExpander() {
        try {
            expander = new VariableExpander( new MockDbManager() );
        } catch( IllegalAccessException e ) {

        } catch( InstantiationException e ) {

        } catch( NoSuchMethodException e ) {

        } catch( InvocationTargetException e ) {

        }
    }

    @Test
    public void TestSingleNounExpansion() {
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
    public void TestMultipleNounExpansion() {
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
    public void TestWhoExpansion() {
        Assert.assertEquals(
            "I am testName",
            expander.expandVariables(
                "testName",
                null,
                "I am $who"
            )
        );
    }

    //We get a null pointer exception, but if we fix it with the code below
    //We get a stack overflow... apparently its a known bug with PowerMockito
    @Test( expected = NullPointerException.class )
    public void TestSomeoneExpansion() throws SkypeException {
        Chat chatContext = PowerMockito.mock( Chat.class );
        User user = PowerMockito.mock( User.class );

        //PowerMockito.suppress( PowerMockito.methods( User.class, "equals" ));
        //when( user.getFullName() ).thenReturn( "firstName lastName" );

        when( chatContext.getAllActiveMembers() ).thenReturn( new User[]{ user } );
        Assert.assertEquals(
            "Someone is: firstName",
            expander.expandVariables(
                "testName",
                chatContext,
                "Someone is: $someone"
            )
        );
    }
}
