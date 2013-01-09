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

class TestRss:

    def setup_class(self):
        self.b = testclient.Bot()
        os.system("rm tests/testResponses.db")
        testclient.initDB('tests/testResponses.db')

    def teardown_class(self):
        self.b.close()

    def setup_method(self, method):
        os.system("echo '' > tests/testResponses")

    @params([
        dict(query="test",
             feedId="1",
             feedURL="testFeed"),
    ])
    def test_verifyEntryCreation(self, query, feedId, feedURL):
        self.b.checkForResponse(msg("bucket, rss \"{0}\" \"{1}\"".format(
                                    query, feedURL)))
        db = sqlite3.connect('tests/testResponses.db')
        c = db.cursor()
        c.execute('SELECT * FROM rss WHERE rssId = "{0}" AND feed = "{1}"'.format(
                  feedId, feedURL))
        assert c.fetchone()
        c.execute('SELECT * FROM responses WHERE query = "{0}" AND rssId = "{1}"'.format(
                  query, feedId))
        assert c.fetchone()
