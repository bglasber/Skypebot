import Skype4Py
import sys
import threading
import time
import sqlite3
import re
import logging
import logging.config
from command import Command
from commandhandlers import rememberHandler
from commandhandlers import forgetHandler
from commandhandlers import addHandler
from commandhandlers import arbitraryCommandHandler
from commandhandlers import responseHandler
from commandhandlers import whatHandler
from commandhandlers import itemHandler

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

def simpleHandler(msg, event):
    """SimpleHandler: Used to handle incoming messages. When a
    message is received via skype, this method is executed"""

    if event == u"RECEIVED" or event == u"SENT":
        logger.info("Received Message - {0}: {1}".format(msg.FromDisplayName, msg.Body))
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
        elif "gives bucket" in msg.Body:
            itemHandler(msg)
        else: 
	    wasCommand = False
	    if msg.Body.startswith("bucket, "):
		wasCommand = arbitraryCommandHandler(msg)
   	    if not wasCommand:
		if msg.FromDisplayName != BOT_DISPLAY_NAME:
		    responseHandler(msg)
			
def createTablesIfNecessary(database):
    """Create the required tables in the database if they don't already exist"""
    db = sqlite3.connect(database)
    c = db.cursor()
    logger.debug("Creating tables that don't exists")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='responses'")
    if not c.fetchone():
        logger.info("Creating the responses tablex...")
        c.execute("CREATE TABLE responses ( query text collate nocase, responses )")
        c.execute("CREATE INDEX responses_index ON responses ( query collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='quotes'")
    if not c.fetchone():
        logger.info("Creating the quotes table...")
        c.execute("CREATE TABLE quotes ( username, quote )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='nouns'")
    if not c.fetchone():
        logger.info("Creating the nouns table...")
        c.execute("CREATE TABLE nouns ( noun text collate nocase )")
        c.execute("CREATE INDEX nouns_index ON nouns ( noun collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='verbs'")
    if not c.fetchone():
        logger.info("Creating the verbs table...")
        c.execute("CREATE TABLE verbs ( verb text collate nocase )")
        c.execute("CREATE INDEX verbs_index ON verbs ( verb collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='presentVerbs'")
    if not c.fetchone():
        logger.info("Creating the presentVerbs table...")
        c.execute("CREATE TABLE presentVerbs ( verb text collate nocase )")
        c.execute("CREATE INDEX presentVerbs_index ON presentVerbs ( verb collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pastVerbs'")
    if not c.fetchone():
        logger.info("Creating the pastVerbs table...")
        c.execute("CREATE TABLE pastVerbs ( verb text collate nocase )")
        c.execute("CREATE INDEX pastVerbs_index ON pastVerbs ( verb collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='ingVerbs'")
    if not c.fetchone():
        logger.info("Creating the ingVerbs table...")
        c.execute("CREATE TABLE ingVerbs ( verb text collate nocase )")
        c.execute("CREATE INDEX ingVerbs_index ON ingVerbs ( verb collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='adjectives'")
    if not c.fetchone():
        logger.info("Creating the adjectives table...")
        c.execute("CREATE TABLE adjectives ( adjective text collate nocase )")
        c.execute("CREATE INDEX adjectives_index ON adjectives( adjective collate nocase )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pluralNouns'")
    if not c.fetchone():
        logger.info("Creating the pluralNouns table...")
        c.execute("CREATE TABLE pluralNouns ( noun text collate nocase )")
        c.execute("CREATE INDEX pluralNouns_index ON pluralNouns ( noun collate nocase )")
    logger.debug("Finished checking for tables")
    db.close()

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
            logger.ERROR("Could not connect to skype!\n")
            sys.exit(1)
        else:
            s.OnMessageStatus = handler
            try:
                while True:
                    time.sleep(1)
            except KeyboardInterrupt:
                c = Command(None)
                c.closeDB()
                sys.exit(0)

b = Bot()
initDB(DATABASE_NAME)
b.connectAndListen(simpleHandler)
