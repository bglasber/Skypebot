import testclient
import pytest
import sqlite3
import os
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
            if line.strip() == response:
                return True
    return False


class TestReponses:

    def setup_class(self):

        self.b = testclient.Bot()
        os.system("rm tests/botChatDump 2>/dev/null")
        os.system("rm tests/testResponses.db 2>/dev/null")
        testclient.initDB('tests/testResponses.db')
    
    def teardown_class(self):

        self.b.close()

    
    @params([
        dict(triggerResponseTuple=("test", "test")),
        dict(triggerResponseTuple=("2", "2")),
        dict(triggerResponseTuple=("I'm a banana", "woot")),
        dict(triggerResponseTuple=("OneLastOne", "This is the result")),
    ])
    def test_basicResponseInsertion(self, triggerResponseTuple):
        
        self.b.checkForResponse(msg("bucket, add \"{0}\" \"{1}\"".format(*triggerResponseTuple)))
        self.b.checkForResponse(msg(triggerResponseTuple[0]))
        assert checkFileForResponse("tests/botChatDump", triggerResponseTuple[1])
        os.system('rm tests/botChatDump')




