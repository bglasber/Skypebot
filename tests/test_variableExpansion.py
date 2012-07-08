import testclient
import pytest
import sqlite3
import os
from msg import msg

def checkFileForResponse(fileName, response):
    with open(fileName) as f:
        for line in f:
            if line.strip() == response:
                return True
    return False


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

class TestExpansion:

    def setup_class(self):
       self.b = testclient.Bot()
       os.system("rm tests/testResponses.db")
       testclient.initDB('tests/testResponses.db')

    def teardown_class(self):
        self.b.close()

    def setup_method(self, method):
        os.system("echo '' > tests/testResponses")

    def test_variableExpansion(self):
        self.b.checkForResponse(msg("bucket, noun+ noun"))
        self.b.checkForResponse(msg("bucket, nouns+ nouns"))
        self.b.checkForResponse(msg("bucket, verb+ verb"))
        self.b.checkForResponse(msg("bucket, verbs+ verbs"))
        self.b.checkForResponse(msg("bucket, verbed+ verbed"))
        self.b.checkForResponse(msg("bucket, verbing+ verbing"))
        self.b.checkForResponse(msg("bucket, adjective+ adjective"))
        self.b.checkForResponse(msg("bucket, place+ place"))
        self.b.checkForResponse(msg("bucket, add \"test\" \"$noun $nouns $verb $verbs $verbed $verbing $adjective $place\""))
        self.b.checkForResponse(msg("test"))
        assert checkFileForResponse("tests/botChatDump", "noun nouns verb verbs verbed verbing adjective place")

