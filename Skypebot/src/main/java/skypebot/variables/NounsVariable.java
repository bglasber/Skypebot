package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 5:44 PM
 */
public class NounsVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public NounsVariable( IDbManager manager ) {
        dbManager = manager;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$nouns" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String noun = pluralizeNoun(
                dbManager.getSingleFromDb(
                    dbManager.getSchema().getNounTable(),
                    "noun"
                )
            );

            logger.trace( "trying to replace \"" + message + "\" with " + noun );
            return message.replaceFirst(
                "\\$nouns",
                noun
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

    public String pluralizeNoun( String noun ) {
        //Isolate the word to modify in a clause
        //
        //Extract words
        ArrayList<String> words = new ArrayList<String>();

        int lastIndex = 0;
        for( int i = 0; i < noun.length(); i++ ) {
            if( noun.substring(
                i,
                i + 1
            ).compareTo( " " ) == 0 ) {
                words.add(
                    noun.substring(
                        lastIndex,
                        i
                    ).trim()
                );
                lastIndex = i + 1;
            }
        }
        words.add( noun.substring( lastIndex ) );

        //pluralize appropriate word
        boolean hasBeenSet = false;
        for( int i = 0; i < words.size(); i++ ) {
            String current = words.get( i ).toLowerCase();

            if( current.compareTo( "of" ) == 0 ||
                current.compareTo( "on" ) == 0 ||
                current.compareTo( "with" ) == 0 ) {

                if( i > 0 ) {
                    words.set(
                        i - 1,
                        pluralize( words.get( i - 1 ) )
                    );
                    hasBeenSet = true;
                }
            }
        }

        //if special conditions are not met, pluralize last word in clause
        if( !hasBeenSet ) {
            words.set(
                words.size() - 1,
                pluralize( words.get( words.size() - 1 ) )
            );
        }

        //rebuild phrase before returning
        String pluralNoun = "";
        for( String x : words ) {
            pluralNoun += ( x + " " );
        }

        return pluralNoun.trim();
    }

    public String pluralize( String noun ) {
        String nounLow = noun.toLowerCase();
        int len = noun.length();

        if( noun.length() < 1 ) {
            return noun; // return if noun is blank
        }
        if( noun.length() < 2 ) {
            return ( noun + "'s" ); //if noun is too short, just add ['s] to the end
        }

        //Check special cases
        if( nounLow.compareTo( "moose" ) == 0 ) {
            return noun;

        }
        else if( nounLow.compareTo( "goose" ) == 0 ) {
            return "geese";

        }
        else if( nounLow.compareTo( "mouse" ) == 0 ) {
            return "mice";

        }
        else if( nounLow.compareTo( "ox" ) == 0 ) {
            return "oxen";

        }
        else if( nounLow.compareTo( "man" ) == 0 ) {
            return "men";

        }
        else if( nounLow.compareTo( "woman" ) == 0 ) {
            return "women";

        }
        else if( nounLow.compareTo( "child" ) == 0 ) {
            return "children";

        }
        else if( nounLow.compareTo( "tooth" ) == 0 ) {
            return "teeth";

        }
        else if( nounLow.compareTo( "foot" ) == 0 ) {
            return "feet";

        }
        else if( nounLow.compareTo( "mud" ) == 0 ) {
            return "mud";

        }
        else if( nounLow.compareTo( "grass" ) == 0 ) {
            return "grass";

        }
        else if( nounLow.compareTo( "bison" ) == 0 ) {
            return "bison";

        }
        else if( nounLow.compareTo( "fish" ) == 0 ) {
            return "fish";

        }
        else if( nounLow.compareTo( "elk" ) == 0 ) {
            return "elk";

        }
        else if( nounLow.compareTo( "deer" ) == 0 ) {
            return "deer";

        }
        else if( nounLow.compareTo( "people" ) == 0 ) {
            return "feet";

        }
        else if( nounLow.compareTo( "buffalo" ) == 0 ) {
            return "buffalo";

        }
        else if( nounLow.compareTo( "sheep" ) == 0 ) {
            return "sheep";

        }
        else if( nounLow.compareTo( "salmon" ) == 0 ) {
            return "salmon";

        }
        else if( nounLow.compareTo( "plankton" ) == 0 ) {
            return "plankton";

        }
        else if( nounLow.compareTo( "squid" ) == 0 ) {
            return "squid";

        }
        else if( nounLow.compareTo( "die" ) == 0 ) {
            return "dice";

        }

        //isolate last 1 and 2 letters in the noun
        String last1 = nounLow.substring(
            len - 1,
            len
        );
        String last2 = nounLow.substring(
            len - 2,
            len
        );


        //compare letter combo with rules
        if( last1.compareTo( "f" ) == 0 ) {
            return ( noun.substring(
                0,
                len - 1
            ) + "ves" );

        }
        else if( last2.compareTo( "fe" ) == 0 ) {
            return ( noun.substring(
                0,
                len - 2
            ) + "ves" );

        }
        else if( last2.compareTo( "us" ) == 0 ) {
            return ( noun.substring(
                0,
                len - 2
            ) + "i" );

        }
        else if( last2.compareTo( "sh" ) == 0 ) {
            return ( noun + "es" );

        }
        else if( last2.compareTo( "ch" ) == 0 ) {
            return ( noun + "es" );

        }
        else if( last1.compareTo( "s" ) == 0 ) {
            return ( noun + "es" );

        }
        else if( last1.compareTo( "x" ) == 0 ) {
            return ( noun + "es" );

        }
        else if( last1.compareTo( "y" ) == 0 ) {
            if( isVowel(
                noun.substring(
                    len - 2,
                    len - 1
                )
            ) ) {
                return ( noun + "s" );
            }
            else {
                return ( noun.substring(
                    0,
                    len - 1
                ) + "ies" );
            }

        }
        else if( last1.compareTo( "o" ) == 0 ) {
            if( isVowel(
                noun.substring(
                    len - 2,
                    len - 1
                )
            ) ) {
                return ( noun + "s" );
            }
            else {
                return ( noun + "es" );
            }

        }
        else {
            return ( noun + "s" );

        }
    }

    public Boolean isVowel( String letter ) {
        if( letter.compareTo( "a" ) == 0 ||
            letter.compareTo( "e" ) == 0 ||
            letter.compareTo( "i" ) == 0 ||
            letter.compareTo( "o" ) == 0 ||
            letter.compareTo( "u" ) == 0 ) {

            return true;
        }
        else {
            return false;
        }
    }
}
