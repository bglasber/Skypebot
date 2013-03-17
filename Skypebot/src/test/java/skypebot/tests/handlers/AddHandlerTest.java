package skypebot.tests.handlers;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.Skype;
import com.skype.SkypeException;
import org.junit.Assert;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import skypebot.handlers.AddHandler;
import skypebot.tests.mock.MockDbManager;

import static org.mockito.Mockito.*;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 6:01 PM
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { Chat.class, ChatMessage.class } )
public class AddHandlerTest {

    private AddHandler addHandler;
    private ChatMessage message;

    @Before
    public void setup() throws SkypeException {
        addHandler = new AddHandler();
        addHandler.setManager( new MockDbManager() );
        message = PowerMockito.mock( ChatMessage.class );
        Chat mockedChat = PowerMockito.mock( Chat.class );
        when( mockedChat.send( anyString() ) ).thenReturn( message );
        when( message.getChat() ).thenReturn( mockedChat );
    }

    @Test
    public void testCanHandle() throws Exception {
        when( message.getContent() ).thenReturn( "bucket, add 'query' 'response'" );
        Assert.assertTrue( addHandler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "bucket, add 'query 'response'" );
        Assert.assertFalse( addHandler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "bakflk, add 'query' 'response'" );
        Assert.assertFalse( addHandler.canHandle( message ) );
        when( message.getContent() ).thenReturn( "bucket, add query response" );
        Assert.assertFalse( addHandler.canHandle( message ) );

    }

}
