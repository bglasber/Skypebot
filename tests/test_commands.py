import testclient
import pytest
import sqlite3
import os
import re
from msg import msg


def params(funcarglist):
    def wrapper(function):
        function.funcarglist = funcarglist
        return function
    return wrapper
                          
def iterate(numIterations):
    def wrapper(function):
        function.iterations = numIterations
        return function
    return wrapper
                                                  
def pytest_generate_tests(metafunc):
    if getattr(metafunc.function, "iterations", {}):
        for i in range(int(getattr(metafunc.function, "iterations", {}))):
            metafunc.addcall()
    else:
        for funcargs in getattr(metafunc.function, "funcarglist", {}):
            metafunc.addcall(funcargs = funcargs)

def checkFileForResponse(fileName, response):
    with open(fileName) as f:
        for line in f:
            if response in line:
                return True
    return False

class TestCommands:

    def setup_class(self):
        self.b = testclient.Bot()
        os.system("rm tests/botChatDump 2>/dev/null")
        os.system("rm tests/testResponses.db 2>/dev/null")
        testclient.initDB('tests/testResponses.db')

    def setup_method(self, method):
        os.system("echo '' > tests/botChatDump")

    def teardown_class(self):
        self.b.close()

    @params([
        dict(preambleCommands=["gives bucket car", "gives bucket orange"],
             commandToTest="bucket, inventory", 
             linesToLookFor=[" - car", " - orange"]),
    ])
    def test_inventory(self, preambleCommands, commandToTest, linesToLookFor):
        for command in preambleCommands:
            self.b.checkForResponse(msg(command))
        if commandToTest:
            self.b.checkForResponse(msg(commandToTest))
        for line in linesToLookFor:
            assert checkFileForResponse("tests/botChatDump", line)

    @params([
        dict(preambleCommands=['blahblahblah'],
            # Because the msg handler is limited, just the fact that
            # we get a message back indicates success
             linesToLookFor=["TestDisplayName"])
    ])
    def test_rememberThat(self, preambleCommands, linesToLookFor):
        for command in preambleCommands:
            self.b.checkForResponse(msg(command,1))
        self.b.checkForResponse(msg("bucket, remember that", 2))
        for line in linesToLookFor:
            assert checkFileForResponse("tests/botChatDump", line)

    @params([
        dict(preambleCommands=['bucket, add "stuff1" "stuff2"'],
             triggersToSend=['stuff1'],
             linesToLookFor=['stuff2'],
             linesForgotten=['stuff2']
        ),
        dict(preambleCommands=['bucket, add "woot" "woot"',
                               'bucket, add "final" "final"'],
             triggersToSend=['woot', 'final'],
             linesToLookFor=['woot', 'final'],
             linesForgotten=['final']
        )
    ])
    def test_forgetThat(self, preambleCommands, triggersToSend, linesToLookFor,
                        linesForgotten):
        for command in preambleCommands:
            self.b.checkForResponse(msg(command))
        for trigger in triggersToSend:
            self.b.checkForResponse(msg(trigger))
        for line in linesToLookFor:
            assert checkFileForResponse("tests/botChatDump", line)
        self.b.checkForResponse(msg("bucket, forget that"))
        os.system('echo "" > tests/botChatDump')
        for trigger in triggersToSend:
            self.b.checkForResponse(msg(trigger))
        for line in linesForgotten:
            assert not checkFileForResponse("tests/botChatDump", line)
        os.system('echo "" > tests/botChatDump')

    @params([
        dict(preambleCommands=['bucket, add "banana" "banana"', ],
             triggersToSend=["banana","bucket, what was that"], 
             lineToLookFor="\"banana\" -> \"banana\""),
        dict(preambleCommands=['bucket, add "vanilla" "vanilla"' ],
             triggersToSend=["banana", "vanilla", "bucket, what was that"],
             lineToLookFor="\"vanilla\" -> \"vanilla\"",
        ),
        dict(preambleCommands=[],
             triggersToSend=["vanilla", "gives bucket banana", "bucket, what was that"],
             lineToLookFor="\"vanilla\" -> \"vanilla\"",
        )
    ])
    def test_whatWasThat(self, preambleCommands, triggersToSend, lineToLookFor):
        for command in preambleCommands:
            self.b.checkForResponse(msg(command))
        for trigger in triggersToSend:
            self.b.checkForResponse(msg(trigger))
        assert checkFileForResponse("tests/botChatDump", lineToLookFor)
    
    @params([
        dict(preambleCommands=['bucket, adjective+ awesome',
                               'bucket, noun+ sauce'],
             triggerToSend="AAS",
             lineToLookFor="awesome awesome sauce",
        )
    ])
    def test_tlaAndBandName(self, preambleCommands, triggerToSend, lineToLookFor):

        for command in preambleCommands:
            self.b.checkForResponse(msg(command))
        self.b.checkForResponse(msg(triggerToSend))
        assert checkFileForResponse("tests/botChatDump", lineToLookFor)
        self.b.checkForResponse(msg(lineToLookFor))
        assert checkFileForResponse("tests/botChatDump", "good name for a band")
       





