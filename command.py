import random
import sqlite3
import sys
from variableexpander import VariableExpander
from wordhandler import WordHandler
import pdb
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
        if optArg3:
            self.parsedCommand = [ optArg3, optArg4 ]

    def connectToDB(self, dbName):
        """Connect to the database if we aren't already connected"""
        if not Command.database:
            Command.database = sqlite3.connect(dbName, check_same_thread=False)
            Command.databaseCursor = Command.database.cursor()

    def closeDB(self):
        """Close the database when we are done with it"""
        Command.database.close()

    def giveItem(self):

        # Check the number of items and set up variables
        Command.databaseCursor.execute('SELECT count(*) FROM items')
        numItems = Command.databaseCursor.fetchone()[0]
        itemToDelete = None
        itemToInsert = self.parsedCommand[0]
        if numItems > 15:
            Command.databaseCursor.execute('SELECT item FROM items ORDER BY RANDOM() LIMIT 1')
            itemToDelete = Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(itemToDelete))
            Command.database.commit()

        # Check for duplicates
        Command.databaseCursor.execute('SELECT item FROM items WHERE item = "{0}"'.format(itemToInsert))
        if Command.databaseCursor.fetchone():
            return ( "Already Have it", )
        Command.databaseCursor.execute('INSERT INTO items VALUES ( "{0}" )'.format(itemToInsert))
        Command.database.commit()

        # Handle the returns
        if itemToDelete:
            return ( itemToDelete, itemToInsert )
        else:
            return ( itemToInsert, )

    def getResponse(self, msg):
        """grab a response from the database where the query they provided
        has the value of the query column contained in it.
        Only Grab one response, and ensure that it is random"""
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
        return Command.previousMessage

    def isValid(self):
        """Determine if the command is valid or not"""
        args = self.cmd.split(" ")
        if args[0] == "random" and len(args) == 3:
            self.parsedCommand = args
            return True
        elif args[0] == "drop":
            self.parsedCommand = args
            return True
	elif WordHandler(args[0], args[1:]).isValidCommand():
	    self.parsedCommand = args
	    return True
        else:
            return False

    def execute(self):
        """Execute the parsed command and return the output"""
        
        if self.parsedCommand[0] == "random":
            return str(random.randint(int(self.parsedCommand[1]), int(self.parsedCommand[2])))
        elif self.cmd == "add":
            Command.databaseCursor.execute('''INSERT INTO responses VALUES ( "{0}", "{1}")
                    '''.format(self.parsedCommand[0], self.parsedCommand[1]))
            Command.database.commit()
            return "Added: {0} -> {1}".format(self.parsedCommand[0], self.parsedCommand[1])
        elif self.parsedCommand[0] == "drop":
            item = " ".join(self.parsedCommand[1:])
            Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(item))
            Command.database.commit()
            return "/me dropped {0}".format(item)
        else:
            return WordHandler(self.parsedCommand[0], self.parsedCommand[1:]).writeChanges()

    def remember(self):
        """Insert the quote into the quotes database and commit it"""
        Command.databaseCursor.execute('''INSERT INTO quotes VALUES ( '{0}', '{1}' )'''.format(
                                       self.parsedCommand[0], self.parsedCommand[1]))
        Command.database.commit()
    def forgetThat(self):
        """Find the quote in the appropriate table, and delete it"""
        # Forget the response put into the response database
        # This should work on its own skype instance, we just need to strip off the BUCKETBOT::
        # Again, change the name to the bots public name, probably bucket
        Command.databaseCursor.execute('''DELETE FROM responses WHERE "{0}" LIKE "%" || query || "%" AND responses = "{1}"
                                       '''.format(Command.previousMessage[0], Command.previousMessage[1]))
        Command.database.commit()

    def itemsInBucket(self, msg):
        """Print out all of the items in the bucket"""
        Command.databaseCursor.execute('SELECT item FROM items')
        for item in Command.databaseCursor.execute('SELECT item FROM items'):
            msg.Chat.SendMessage(" - " + item[0].encode('ascii', 'ignore'))

