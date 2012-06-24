import re
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
    prevMessage = findPreviousChatMessage(msg.Id, msg.Chat)
    c = Command("forget", prevMessage.FromDisplayName, prevMessage.Body)
    c.forget()
    msg.Chat.SendMessage("okay {0}, forgetting '{1}: {2}'".format(
        msg.FromDisplayName, prevMessage.FromDisplayName, 
        prevMessage.Body))

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

def responseHandler(msg):
    """Handle gathering a response from the database and return it"""
    c = Command(msg.Body)
    response = c.getResponse(msg)
    if response:
        msg.Chat.SendMessage(response)




