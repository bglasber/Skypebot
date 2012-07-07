import Skype4Py
import sys
import threading
import time
import sqlite3
import re
import logging
import logging.config
from schemaConstructor import SchemaConstructor
from command import Command
from commandHandlers import rememberHandler
from commandHandlers import forgetHandler
from commandHandlers import addHandler
from commandHandlers import arbitraryCommandHandler
from commandHandlers import responseHandler
from commandHandlers import whatHandler
from commandHandlers import itemHandler
from commandHandlers import tlaHandler

######################## CONFIGURE THESE ##############################
# Bot Display Name
BOT_DISPLAY_NAME = "Bucket"
# Database to connect to (SQLite3 uses files)
DATABASE_NAME = "responses.db"
# Configuration file for logging module in python
# Title of the logger in this file
l = logging.getLogger('Skype4Py.api.posix_x11.SkypeAPI')
l.setLevel(logging.WARN)
logger = logging.getLogger('MainClient')
logging.config.fileConfig('logger.cfg', None, False)
#######################################################################

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
        logger.debug("Received Message - {0}: {1}".format(msg.FromDisplayName, msg.Body))
        if msg.Body == "bucket, remember that":
            rememberHandler(msg)
        elif msg.Body == "bucket, forget that":
            forgetHandler(msg)
        elif msg.Body == "bucket, what was that":
            whatHandler(msg)
        elif msg.Body.startswith("bucket, add"):
            addHandler(msg)
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
        """Construct the default elements for bot class"""
        self.chat = None

    def connectAndListen(self, handler):
        """Set the transport handler, attach to the skype
        instance, and listen for input"""
        s = Skype4Py.Skype(Transport='x11')
        try:
            s.Attach()
        except:
            logger.error("Could not connect to skype!")
            logger.error("Verify that skype is launched and that the program is allowing us to connect")
            sys.exit(1)
        else:
            s.OnMessageStatus = handler
            try:
                while True:
                    time.sleep(1)
            except KeyboardInterrupt:
                c = Command(None)
                c.closeDB()
                logger.info("Terminating program...")
                sys.exit(0)

b = Bot()
initDB(DATABASE_NAME)
logger.info("Beginning main execution")
b.connectAndListen(simpleHandler)
