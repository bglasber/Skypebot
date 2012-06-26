import re
import sys
from command import Command

def findPreviousChatMessage(messageId, chat):
    """Iterate through all the recent chat messages, and return the
    one who is right before the messageID we are searching for.
    Used for the "remember that" command"""

    for i in xrange(len(chat.RecentMessages)):
        if chat.RecentMessages[i].Id == messageId:
            if i != 0:
                return chat.RecentMessages[i-1]
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
    c = Command(None)
    prev = c.getWhatWasThat()
    msg.Chat.SendMessage("It was: \"{0}\" -> \"{1}\"".format(*prev))
        

def addHandler(msg):
    """Handle the adding of responses to the response database"""
    
    parsedLine = re.sub(r'bucket, add "([^"]+)" "([^"]+)"', 
                                r'\1|\2', msg.Body).split("|")
    c = Command("add", parsedLine[0], parsedLine[1])
    msg.Chat.SendMessage(c.execute())

def arbitraryCommandHandler(msg):
    """Handle the arbitrary execution of commands, most new commands wil be executed through
    this handler"""

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
    sys.stdout.write(item+"\r\n")
    c = Command("give", item)
    c.giveItem()
    msg.Chat.SendMessage("/me is now holding {0}".format(item))




