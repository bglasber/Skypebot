package skypebot.tests.handlers;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.SkypeException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import skypebot.handlers.addVariableHandlers.AddItemHandler;
import skypebot.tests.mock.MockDbManager;

import static org.mockito.Mockito.*;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 1:34 PM
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ChatMessage.class, Chat.class })
public class AddItemHandlerTest {

    private AddItemHandler handler;
    private ChatMessage message;

    @Before
    public void setup() {
        message = PowerMockito.mock( ChatMessage.class );
        this.handler = new AddItemHandler();
        this.handler.setManager( new MockDbManager() );
    }

    @Test
    public void AddItemHandlerCanHandleTest() throws SkypeException {
        when( message.getContent() ).thenReturn( "/me gives bucket item" );
        Assert.assertTrue( handler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "brad gives bucket item" );
        Assert.assertTrue( handler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "asdf gives bucket item with lots of data" );
        Assert.assertTrue( handler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "asdf gives otherPerson item with lots of data" );
        Assert.assertFalse( handler.canHandle( message ) );
    }

    @Test
    public void AddItemHandlerGetItemTest() throws Exception {
        AddItemHandler newHandler = spy( new AddItemHandler() );
        when( message.getContent() ).thenReturn( "/me gives bucket item" );
        PowerMockito.verifyPrivate( newHandler ).invoke(
            "getItemToAdd",
            message
        );
    }

    @Test
    public void AddItemHandlerHandleTest() throws SkypeException {
        Chat chat = PowerMockito.mock( Chat.class );
        when( chat.send( anyString() ) ).thenReturn( message );
        when( message.getContent() ).thenReturn( "/me gives bucket item to add" );
        when( message.getChat() ).thenReturn( chat );
        handler.handle( message );
    }
}
