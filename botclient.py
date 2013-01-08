import Skype4Py
import sys
import threading
import time
import sqlite3
import re
import logging
import logging.config
import grammar
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
    	msg.Body = msg.Body.strip()
        logger.debug("Received Message - {0}: {1}".format(msg.FromDisplayName, msg.Body))
        if msg.Body.lower() == "bucket, remember that":
            rememberHandler(msg)
        elif msg.Body.lower() == "bucket, forget that":
            forgetHandler(msg)
        elif msg.Body.lower() == "bucket, what was that":
            whatHandler(msg)
        elif msg.Body.lower().startswith("bucket, add"):
            addHandler(msg)
        elif msg.Body.lower().startswith("bucket, rss"):
            rssHandler(msg)
        elif msg.Body.lower().startswith("bucket, inv"):  # INVENTORY
            c = Command(None)
            c.itemsInBucket(msg)
        elif msg.Body..lower()startswith("bucket, video"):
	    c = Command(None)
            c.videosInBucket(msg)
        elif re.search(r"^[A-Z]{3}\??$", msg.Body):
            tlaHandler(msg);
        elif "gives bucket" in msg.Body.lower():
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
