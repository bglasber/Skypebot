import re
import sys
import logging
import logging.config
from command import Command

logger = logging.getLogger('handlers')


def findPreviousChatMessage(messageId, chat):
    """Iterate through all the recent chat messages, and return the
    one who is right before the messageID we are searching for.
    Used for the "remember that" command"""

    logger.debug("Iterating through {0} messages to find ID".format(len(chat.RecentMessages)))
    reverseMessages = chat.RecentMessages[::-1]
    for i in xrange(len(reverseMessages)):
        if reverseMessages[i].Id == messageId:
            if i != len(reverseMessages):
                return reverseMessages[i+1]
            else: 
                return None 
    return None


def rememberHandler(msg):
    """Handle the remembering process"""
    prevMessage = findPreviousChatMessage(msg.Id, msg.Chat)
    c = Command("remember", prevMessage.FromDisplayName, prevMessage.Body) 
    msg.Chat.SendMessage("remembering '{0}: {1}'".format(
                         prevMessage.FromDisplayName, prevMessage.Body))
    c.remember()

def forgetHandler(msg):
    """Handle the forgetting process"""
    c = Command(None)
    c.forgetThat()
    msg.Chat.SendMessage("okay, forgetting it...")

def whatHandler(msg):
    """Handle the what process"""
    logger.debug('Handling message using "what handler"')
    c = Command(None)
    prev = c.getWhatWasThat()
    msg.Chat.SendMessage("It was: \"{0}\" -> \"{1}\"".format(*prev))
        

def addHandler(msg):
    """Handle the adding of responses to the response database"""
    
    parsedLine = re.sub(r'bucket, add "([^"]+)" "([^"]+)"', 
                                r'\1|\2', msg.Body).split("|")
    # Catch broken add syntax
    try:
        c = Command("add", parsedLine[0], parsedLine[1])
        msg.Chat.SendMessage(c.execute())
    except IndexError:
        pass

def arbitraryCommandHandler(msg):
    """Handle the arbitrary execution of commands, most new commands wil be executed through
    this handler. Passes the command into the command class - begins execution and does stuff"""

    c = Command(msg.Body[8:])

    if c.isValid():
        msg.Chat.SendMessage(c.execute())
	return True
    else:
	return False

def responseHandler(msg):
    """Handle gathering a response from the database and return it"""
    c = Command(msg.Body)
    response = c.getResponse(msg)
    if response:
        msg.Chat.SendMessage(response)

def itemHandler(msg):
    """Take the item and put it into the bucket"""
    item = re.sub(r".*gives bucket ", "", msg.Body)
    c = Command("give", item)
    returnedOutput = c.giveItem()
    if returnedOutput[0] == "Already Have it":
        msg.Chat.SendMessage("I'm already holding {0}!".format(item))
    elif len(returnedOutput) == 2:
        msg.Chat.SendMessage("/me drops {0} and picks up {1}".format(*returnedOutput))
    else:
        msg.Chat.SendMessage("/me is now holding {0}".format(*returnedOutput))

def inventoryHandler(msg):
    """Display the items in the inventory"""
    c = Command(None)
    c.itemsInBucket(msg)
    
def videoURLHandler(msg):
    """Add a video URL to the database"""
    urlStart = msg.Body.lower().find("http://")
    url = ""

    urlFound = False
    x = urlStart
    while(not urlFound):
        if msg[x:x+1] == " " or msg[x:x+1] == "":
            url = msg[urlStart:x]
            urlFound = True
        x += 1
    
    c = Command(None)
    c.insertLink(msg.FromDisplayName, url, "VIDEO")
            
def randomVideoHandler(msg):
    """Display a random youtube video URL"""
    c = Command(None)
    c.getVideoURL(msg)

def tlaHandler(msg):
    """Find three random things in the database that begin with the appropriate letters"""

    c = Command(None)
    response = ""
    word = c.getAcronymLetter("adjectives", "adjective", msg.Body[0])
    if word:
        response += word + " "
    else:
        return
    word = c.getAcronymLetter("adjectives", "adjective", msg.Body[1])
    if word:
        response += word + " "
    else:
        return
    word = c.getAcronymLetter("nouns", "noun", msg.Body[2])
    if word:
        logger.debug("The full expansion was constructed - Inserting into table...")
        response += word
        c.insertBandName(response)
        msg.Chat.SendMessage(response)
    else:
        return

def rssHandler(msg):
    """Insert the feed into the rss table and put the rssId into the responses table"""
    parsedLine = re.sub(r'bucket, rss "([^"]+)" "([^"]+)"', 
                                r'\1|\2', msg.Body).split("|")
    try:
        c = Command(None) 
	c.createRssFeedResponse(parsedLine)
	msg.Chat.SendMessage("added rss feed sucessfully")
    except:
	msg.Chat.SendMessage("Failed to add rss feed!")
