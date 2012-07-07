class Chat:

    RecentMessages = None
    def __init__(self):
        pass

    def SendMessage(self,stringToSend):
        with open("tests/botChatDump", "a") as f:
            f.write(stringToSend+"\n")
    
class msg:

    Chat = Chat()
    Body = None
    Id = 0
    FromDisplayName = "TestDisplayName"

    def __init__(self, text, id=1):
        msg.Body = text
        msg.Id = id

Chat.RecentMessages = [ msg("prevMessage", 1), msg("lastMessage",2) ]
