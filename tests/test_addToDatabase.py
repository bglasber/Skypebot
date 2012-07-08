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

class TestDatabase:

    def setup_class(self):
        self.b = testclient.Bot()
        os.system("rm tests/testResponses.db")
        testclient.initDB('tests/testResponses.db')

    def teardown_class(self):
        self.b.close()

    def setup_method(self, method):
        os.system("echo '' > tests/testResponses")

    @params([
            dict(table="nouns",
                 column="noun",
                 thingToAdd="noun",
                 addCommand="noun+"),
            dict(table="pluralNouns",
                 column="noun",
                 thingToAdd="otherStuff",
                 addCommand="nouns+"),
            dict(table="verbs",
                 column="verb",
                 thingToAdd="verb",
                 addCommand="verb+"),
            dict(table="presentverbs",
                 column="verb",
                 thingToAdd="verb",
                 addCommand="verbs+"),
            dict(table="ingVerbs",
                 column="verb",
                 thingToAdd="verbing",
                 addCommand="verbing+"),
            dict(table="pastVerbs",
                 column="verb",
                 thingToAdd="verbed",
                 addCommand="verbed+"),
            dict(table="adjectives",
                 column="adjective",
                 thingToAdd="adjective",
                 addCommand="adjective+"),
            dict(table="places",
                 column="place",
                 thingToAdd="place",
                 addCommand="place+")
    ])
    def test_addStuff(self, table, column, thingToAdd, addCommand):
        self.b.checkForResponse(msg("bucket, {0} {1}".format(addCommand, thingToAdd)))
        db = sqlite3.connect('tests/testResponses.db')
        c = db.cursor()
        c.execute('''SELECT {0} FROM {1} where {0} = "{2}"'''.format(column, table, thingToAdd))
        assert c.fetchone()

