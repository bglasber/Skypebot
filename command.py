import random
import sqlite3
import sys
from variableexpander import VariableExpander

class Command:

    # Class variables, should be static
    database = None
    databaseCursor = None

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

    def getResponse(self, msg):
        """grab a response from the database where the query they provided
        has the value of the query column contained in it.
        Only Grab one response, and ensure that it is random"""
        Command.databaseCursor.execute('''SELECT responses FROM responses 
                                       WHERE '{0}' LIKE '%' || query || '%'  
                                       ORDER BY RANDOM() LIMIT 1
                                       '''.format(self.cmd))
        resp = Command.databaseCursor.fetchone()
        if resp:
            resp = resp[0].encode('ascii', 'ignore')
            ex = VariableExpander(resp,msg)
            return ex.expandVariables()
        else:
            return None

    def isValid(self):
        """Determine if the command is valid or not"""
        args = self.cmd.split(" ")
        if args[0] == "random" and len(args) == 3:
            self.parsedCommand = args
            return True
        elif args[0] == "verb+":
            self.parsedCommand = args
            return True
        elif args[0] == "verbs+":
            self.parsedCommand = args
            return True
        elif args[0] == "verbing+":
            self.parsedCommand = args
            return True
        elif args[0] == "verbed+":
            self.parsedCommand = args
            return True
        elif args[0] == "noun+":
            self.parsedCommand = args
            return True
        elif args[0] == "adjective+":
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
        else:
            if self.parsedCommand[0] == "verb+":
                Command.databaseCursor.execute('''INSERT INTO verbs VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "verbs+":
                Command.databaseCursor.execute('''INSERT INTO presentVerbs VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "verbing+":
                Command.databaseCursor.execute('''INSERT INTO ingVerbs VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "verbed+":
                Command.databaseCursor.execute('''INSERT INTO pastVerbs VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "noun+":
                Command.databaseCursor.execute('''INSERT INTO nouns VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "nouns+":
                Command.databaseCursor.execute('''INSERT INTO pluralNouns VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            elif self.parsedCommand[0] == "adjective+":
                Command.databaseCursor.execute('''INSERT INTO adjectives VALUES ('{0}')
                        '''.format(' '.join(self.parsedCommand[1:])))
            Command.database.commit()
            return "Added: {0}".format(' '.join(self.parsedCommand[1:]))




                
            

    def remember(self):
        """Insert the quote into the quotes database and commit it"""
        Command.databaseCursor.execute('''INSERT INTO quotes VALUES ( '{0}', '{1}' )'''.format(
                                       self.parsedCommand[0], self.parsedCommand[1]))
        Command.database.commit()
    def forget(self):
        """Find the quote in the appropriate table, and delete it"""
        # Forget the response put into the response database
        # This should work on its own skype instance, we just need to strip off the BUCKETBOT::
        # Again, change the name to the bots public name, probably bucket
        if self.parsedCommand[0] == "Brad Glasbergen":
            Command.databaseCursor.execute('DELETE FROM responses WHERE responses="{0}"'.format(self.parsedCommand[1]))
            Command.database.commit()

