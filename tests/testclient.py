import sys
import re
sys.path.append(".")
from lib.schemaConstructor import SchemaConstructor
from lib.command import Command
from lib.commandHandlers import rememberHandler
from lib.commandHandlers import forgetHandler
from lib.commandHandlers import addHandler
from lib.commandHandlers import arbitraryCommandHandler
from lib.commandHandlers import responseHandler
from lib.commandHandlers import whatHandler
from lib.commandHandlers import itemHandler 
from lib.commandHandlers import tlaHandler
from lib.commandHandlers import rssHandler
BOT_DISPLAY_NAME="Bucket"

def isBandName(bandName):
    if not "\"" in bandName:
        Command.databaseCursor.execute('SELECT name FROM band_names WHERE "{0}" LIKE "%" || name || "%"'.format(
                                   bandName)
        )
        return Command.databaseCursor.fetchone()
    return False

def simpleHandler(msg, event):
    """SimpleHandler: Used to handle incoming messages. When a
    message is received via skype, this method is executed"""

    if event == u"RECEIVED":
        if msg.Body == "bucket, remember that":
            rememberHandler(msg)
        elif msg.Body == "bucket, forget that":
            forgetHandler(msg)
        elif msg.Body == "bucket, what was that":
            whatHandler(msg)
        elif msg.Body.startswith("bucket, add"):
            addHandler(msg)
        elif msg.Body.startswith("bucket, rss"):
            rssHandler(msg)
        elif msg.Body.startswith("bucket, inventory"):
            c = Command(None)
            c.itemsInBucket(msg)
        elif re.search(r"^[A-Z]{3}\??$", msg.Body):
            tlaHandler(msg);
        elif "gives bucket" in msg.Body:
            itemHandler(msg)
        elif isBandName(msg.Body):
            msg.Chat.SendMessage("That would be a good name for a band")
        else: 
	    wasCommand = False
	    if msg.Body.startswith("bucket, "):
		wasCommand = arbitraryCommandHandler(msg)
   	    if not wasCommand:
		if msg.FromDisplayName != BOT_DISPLAY_NAME:
		    responseHandler(msg)
			
def createTablesIfNecessary(database):
    """Create the required tables in the database if they don't already exist"""
    constructor = SchemaConstructor(database) 
    constructor.constructSchema()
    constructor.close()

def initDB(database):
    """Create the tables if they don't exist, and set up the connection for
    the command class"""

    createTablesIfNecessary(database)
   
    # Initialize the required variables
    commandInitializer = Command(None)
    commandInitializer.connectToDB(database)

class Bot:

    def __init__(self):
        self.chat = None

    def checkForResponse(self,msg):
        simpleHandler(msg, u"RECEIVED")

    def close(self):
        c = Command(None)
        c.closeDB()

