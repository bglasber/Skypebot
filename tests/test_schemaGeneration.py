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


class TestSchema:

    def setup_class(self):
        
        os.system("rm tests/testResponses.db 2>/dev/null")
        os.system("rm tests/botChatDump 2>/dev/null")
        self.b = testclient.Bot()

    def teardown_class(self):
        pass

    @params([
        dict(tablesToCheckFor=[ 'responses',
                                'quotes',
                                'items',
                                'band_names',
                                'nouns',
                                'pluralNouns',
                                'ingVerbs',
                                'pastVerbs',
                                'verbs',
                                'adjectives',
                                'places',
             ]
        )
    ])         
    def test_schemaGeneration(self, tablesToCheckFor):
        """Ensure that we created all of the tables we need"""

        testclient.initDB("tests/testResponses.db")
        conn = sqlite3.connect('tests/testResponses.db')
        c = conn.cursor()
        listOfTables = []
        for table in c.execute('SELECT name FROM sqlite_master WHERE type="table"'):
            listOfTables.append(table[0].encode('ascii', 'ignore'))
        for table in tablesToCheckFor:
            assert table in listOfTables

    @params([
        dict(tablesToCheckFor=[ 'responses',
                                'quotes',
                                'items',
                                'band_names',
                                'nouns',
                                'pluralNouns',
                                'ingVerbs',
                                'pastVerbs',
                                'verbs',
                                'adjectives',
                                'places',
             ]
        )
    ])         
    def test_schemaPersistence(self, tablesToCheckFor):
        """Close the database and reopen it. Verify that our tables are still
        there"""

        self.b.close()
        self.b = testclient.Bot()
        conn = sqlite3.connect('tests/testResponses.db')
        c = conn.cursor()
        listOfTables = []
        for table in c.execute('SELECT name FROM sqlite_master WHERE type="table"'):
            listOfTables.append(table[0].encode('ascii', 'ignore'))
        for table in tablesToCheckFor:
            assert table in listOfTables

