package skypebot.engine;

/**
 * User: brad
 * Date: 10/16/13
 * Time: 11:05 PM
 */
public class PersonMessageTuple {
    private String person;
    private String message;

    public PersonMessageTuple(
        String p,
        String m
    ) {
        person = p;
        message = m;
    }

    public String GetPerson() {
        return person;
    }

    public String GetMessage() {
        return message;
    }
}
