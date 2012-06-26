import Skype4Py
import sys
import threading
import time
import sqlite3
import re
from command import Command
from handlers import rememberHandler
from handlers import forgetHandler
from handlers import addHandler
from handlers import arbitraryCommandHandler
from handlers import responseHandler
from handlers import whatHandler
from handlers import itemHandler

######################## CONFIGURE THESE ##############################
# Bot Display Name
BOT_DISPLAY_NAME = "Bucket"
# Database to connect to (SQLite3 uses files)
DATABASE_NAME = "responses.db"
#######################################################################

def simpleHandler(msg, event):
    """SimpleHandler: Used to handle incoming messages. When a
    message is received via skype, this method is executed"""

    if event == u"RECEIVED" or event == u"SENT":
        sys.stdout.write(msg.Body+"\r\n")
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
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='responses'")
    if not c.fetchone():
        c.execute("CREATE TABLE responses ( query, responses )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='quotes'")
    if not c.fetchone():
        c.execute("CREATE TABLE quotes ( username, quote )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='nouns'")
    if not c.fetchone():
        c.execute("CREATE TABLE nouns ( noun )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='verbs'")
    if not c.fetchone():
        c.execute("CREATE TABLE verbs ( verb )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='presentVerbs'")
    if not c.fetchone():
        c.execute("CREATE TABLE presentVerbs ( verb )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pastVerbs'")
    if not c.fetchone():
        c.execute("CREATE TABLE pastVerbs ( verb )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='ingVerbs'")
    if not c.fetchone():
        c.execute("CREATE TABLE ingVerbs ( verb )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='adjectives'")
    if not c.fetchone():
        c.execute("CREATE TABLE adjectives ( adjective )")
    c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pluralNouns'")
    if not c.fetchone():
        c.execute("CREATE TABLE pluralNouns ( noun )")
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
            sys.stdout.write("Could not connect to skype!\n")
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
