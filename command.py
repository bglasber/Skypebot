import random
import sqlite3
import sys
from variableExpander import VariableExpander
from wordHandler import WordHandler
import logging

class Command:

    # Class variables, should be static
    database = None
    databaseCursor = None
    previousMessage = [ "", "" ]
    items = [ ]

    def __init__(self, cmd, optArg3=None, optArg4=None):
        """Construct the required command instance"""
        self.cmd = cmd
        self.parsedCommand = None
        self.logger = logging.getLogger('command')
        if optArg3:
            self.parsedCommand = [ optArg3, optArg4 ]

    def connectToDB(self, dbName):
        """Connect to the database if we aren't already connected"""
        if not Command.database:
            self.logger.debug("Connecting to the database...")
            Command.database = sqlite3.connect(dbName, check_same_thread=False)
            Command.databaseCursor = Command.database.cursor()

    def closeDB(self):
        """Close the database when we are done with it"""
        self.logger.debug("Closing connection to the database")
        Command.database.close()

    def giveItem(self):

        # Check the number of items and set up variables
        Command.databaseCursor.execute('SELECT count(*) FROM items')
        numItems = Command.databaseCursor.fetchone()[0]
        itemToDelete = None
        itemToInsert = self.parsedCommand[0]
        
        Command.databaseCursor.execute('SELECT item FROM items WHERE item = "{0}"'.format(itemToInsert))
        if Command.databaseCursor.fetchone():
            self.logger.info("Item {0} already in the database, not inserting again.".format(itemToInsert))
            return ( "Already Have it", )

        if numItems > 15:
            Command.databaseCursor.execute('SELECT item FROM items ORDER BY RANDOM() LIMIT 1')
            itemToDelete = Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(itemToDelete))
            Command.database.commit()
            self.logger.info("Found more than 15 items in the items table, dropping {0}".format(itemToDelete))

        Command.databaseCursor.execute('INSERT INTO items VALUES ( "{0}" )'.format(itemToInsert))
        Command.database.commit()
        self.logger.info("Inserted {0} into the items table".format(itemToInsert))

        # Handle the returns
        if itemToDelete:
            return ( itemToDelete, itemToInsert )
        else:
            return ( itemToInsert, )

    def getResponse(self, msg):
        """grab a response from the database where the query they provided
        has the value of the query column contained in it.
        Only Grab one response, and ensure that it is random"""
        resp = None
        if not '"' in  self.cmd:
            Command.databaseCursor.execute('''SELECT responses FROM responses 
                                           WHERE "{0}" LIKE "%" || query || "%" 
                                           ORDER BY RANDOM() LIMIT 1
                                           '''.format(self.cmd))
            resp = Command.databaseCursor.fetchone()
        if resp:
            resp = resp[0].encode('ascii', 'ignore')
            Command.previousMessage = [ self.cmd, resp ]
            ex = VariableExpander(resp,msg)
            return ex.expandVariables()
        else:
            return None

    def getWhatWasThat(self):
        self.logger.debug("Got what was that message - handling")
        return Command.previousMessage


    def isValid(self):
        """Determine if the command is valid or not"""
        self.logger.debug("Determining if command is valid or not")
        args = self.cmd.split(" ")
        if args[0] == "random" and len(args) == 3:
            self.parsedCommand = args
            return True
        elif args[0] == "drop":
            self.parsedCommand = args
            return True
        elif args[0] == "quote":
            self.parsedCommand = args
            return True
	elif WordHandler(args[0], args[1:]).isValidCommand():
	    self.parsedCommand = args
	    return True
        else:
            return False

    def execute(self):
        """Execute the parsed command and return the output"""
        
        self.logger.debug("Executing random/drop/wordAdd command")
        if self.parsedCommand[0] == "random":
            return str(random.randint(int(self.parsedCommand[1]), int(self.parsedCommand[2])))
        elif self.cmd == "add":
            Command.databaseCursor.execute('''INSERT INTO responses VALUES ( "{0}", "{1}")
                    '''.format(self.parsedCommand[0], self.parsedCommand[1]))
            Command.database.commit()
            return "Added: {0} -> {1}".format(self.parsedCommand[0], self.parsedCommand[1])
        elif self.parsedCommand[0] == "quote":
            self.logger.debug("Got quote command - checking for quotes for user {0}".format(self.parsedCommand[1]))
            quote = None
            Command.databaseCursor.execute('''SELECT quote FROM quotes 
                                           WHERE username LIKE "%" || "{0}" || "%"
                                           ORDER BY RANDOM() LIMIT 1'''.format(
                                            self.parsedCommand[1]))
            quote = Command.databaseCursor.fetchone()
            if quote:
                quote = quote[0].encode('ascii', 'ignore')
                return "{0}: {1}".format(self.parsedCommand[1], quote)
            else:
                return "{0} has no quotes.".format(self.parsedCommand[1])
            

        elif self.parsedCommand[0] == "drop":
            item = " ".join(self.parsedCommand[1:])
            Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(item))
            Command.database.commit()
            return "/me dropped {0}".format(item)
        else:
            return WordHandler(self.parsedCommand[0], self.parsedCommand[1:]).writeChanges()

    def remember(self):
        """Insert the quote into the quotes database and commit it"""
        self.logger.info("Remembering quote.")
        Command.databaseCursor.execute('''INSERT INTO quotes VALUES ( '{0}', '{1}' )'''.format(
                                       self.parsedCommand[0], self.parsedCommand[1]))
        Command.database.commit()
    def forgetThat(self):
        """Find the quote in the appropriate table, and delete it"""
        # Forget the response put into the response database
        # This should work on its own skype instance, we just need to strip off the BUCKETBOT::
        # Again, change the name to the bots public name, probably bucket
        self.logger.info("Got forget that command - deleting previous response")
        Command.databaseCursor.execute('''DELETE FROM responses WHERE "{0}" LIKE "%" || query || "%" AND responses = "{1}"
                                       '''.format(Command.previousMessage[0], Command.previousMessage[1]))
        Command.database.commit()

    def itemsInBucket(self, msg):
        """Print out all of the items in the bucket"""
        self.logger.info("Got inventory command - listing all items")
        Command.databaseCursor.execute('SELECT item FROM items')
        for item in Command.databaseCursor.execute('SELECT item FROM items'):
            msg.Chat.SendMessage(" - " + item[0].encode('ascii', 'ignore'))

