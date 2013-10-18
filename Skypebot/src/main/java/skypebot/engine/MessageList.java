package skypebot.engine;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * User: brad
 * Date: 10/16/13
 * Time: 10:34 PM
 */
public class MessageList {

    private static Logger logger = Logger.getLogger( MessageList.class.getCanonicalName() );
    private List<PersonMessageTuple> list;

    public MessageList() {
        list = new LinkedList<>();
    }

    // put the message into the list, keeping a maximum of 20 entries
    //SYNCHRONIZED
    //TODO: make configurable
    public void Put( ChatMessage m ) {
        try {
            Put(
                m.getSender().getFullName(),
                m.getContent()
            );
        } catch( SkypeException e ) {
            return;
        }
    }

    public void Put(
        String fullName,
        String message
    ) {
        synchronized( list ) {

            list.add(
                new PersonMessageTuple(
                    fullName,
                    message
                )
            );
            if( list.size() > 20 ) {
                list.remove( 0 );
            }
        }
    }

    // Find the first entry that matches the provided message.
    private int Get(
        String personToMatch,
        String messageToMatch
    ) {
        logger.trace( "Trying to find: " + personToMatch + " - " + messageToMatch );
        for( int i = 0; i < list.size(); i++ ) {

            logger.trace( i + ": " + list.get( i ).GetPerson() + " - " + list.get( i ).GetMessage() );
            if(
                list.get( i ).GetPerson().equals( personToMatch ) &&
                    list.get( i ).GetMessage().contains( messageToMatch )
                ) {
                return i;
            }
        }
        return -1;
    }

    //Given a person's name, a message to look for, and a number of messages to quote
    //construct a string containing the message
    public String GetMessageToQuote(
        String personToMatch,
        String messageToMatch,
        int numMessagesToQuote
    ) {
        synchronized( list ) {
            int curMessageIndex = Get(
                personToMatch,
                messageToMatch
            );
            if( curMessageIndex == -1 ) {
                return null;
            }
            String messageToQuote = "";
            for( int i = 0; i < numMessagesToQuote; i++ ) {
                messageToQuote = messageToQuote + list.get( curMessageIndex ).GetPerson()
                    + ": " + list.get( curMessageIndex ).GetMessage() + "\n";
                curMessageIndex += 1;
                if( curMessageIndex >= list.size() ) {
                    break;
                }

            }
            return messageToQuote;
        }

    }
}
