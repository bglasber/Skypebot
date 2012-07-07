import re
import sys
import command
import random
import logging

class VariableExpander:
    """Take any variables in a response and fill them with objects
    from the respective tables. Called upon locating a response"""

    def __init__(self, responseBeforeExpansion, message):
        self.resp = responseBeforeExpansion
        self.msg = message
        self.logger = logging.getLogger('variableExpander')
        self.logger.debug("Response before expansion: {0}".format(responseBeforeExpansion))

    def expandVariables(self):
        while "$someone" in self.resp:
            while True:
                person = random.choice(self.msg.Chat.ActiveMembers)
                if person.FullName != "" and person.FullName != "Bucket":
                    break
            person = person.FullName.split(" ")[0]
            self.resp = re.sub(r"\$someone", person, self.resp, 1)
	if "$who" in self.resp:
	    self.resp = re.sub(r"\$who", self.msg.FromDisplayName.split(" ")[0], self.resp)
        while "$nouns" in self.resp:
            self.logger.debug("Found $nouns in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT noun FROM pluralNouns ORDER BY RANDOM() LIMIT 1")
            noun = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$nouns", noun, self.resp, 1) 
        while "$noun" in self.resp:
            self.logger.debug("Found $noun in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT noun FROM nouns ORDER BY RANDOM() LIMIT 1")
            noun = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$noun", noun, self.resp, 1) 
        while "$verbs" in self.resp:
            self.logger.debug("Found $verbs in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT verb FROM presentVerbs ORDER BY RANDOM() LIMIT 1")
            verb = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$verbs", verb, self.resp, 1) 
        while "$verbing" in self.resp:
            self.logger.debug("Found $verbing in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT verb FROM presentVerbs ORDER BY RANDOM() LIMIT 1")
            command.Command.databaseCursor.execute("SELECT verb FROM ingVerbs ORDER BY RANDOM() LIMIT 1")
            verb = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$verbing", verb, self.resp, 1) 
        while "$verbed" in self.resp:
            self.logger.debug("Found $verbed in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT verb FROM pastVerbs ORDER BY RANDOM() LIMIT 1")
            verb = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$verbed", verb, self.resp, 1) 
        while "$verb" in self.resp:
            self.logger.debug("Found $verb in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT verb FROM verbs ORDER BY RANDOM() LIMIT 1")
            verb = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$verb", verb, self.resp, 1) 
        while "$adjective" in self.resp:
            self.logger.debug("Found $adjective in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT adjective FROM adjectives ORDER BY RANDOM() LIMIT 1")
            verb = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$adjective", verb, self.resp, 1) 
        while "$place" in self.resp:
            self.logger.debug("Found $place in response - doing substitution")
            command.Command.databaseCursor.execute("SELECT place FROM places ORDER BY RANDOM() LIMIT 1")
            place = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            self.resp = re.sub(r"\$place", place, self.resp, 1) 

	while "$popitem" in self.resp:
            self.logger.debug("Found $popitem in response - doing substitution")
            command.Command.databaseCursor.execute('SELECT item FROM items ORDER BY RANDOM() LIMIT 1')
            item = command.Command.databaseCursor.fetchone()
	    if not item:
                item = "banana"
            else:
                item = item[0].encode('ascii', 'ignore')
                command.Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(item))
                command.Command.database.commit()
                self.resp = re.sub(r"\$popitem", item, self.resp, 1)
	while "$item" in self.resp:
            self.logger.debug("Found $item in response - doing substitution")
	    command.Command.databaseCursor.execute('SELECT item FROM items ORDER BY RANDOM() LIMIT 1')
            item = command.Command.databaseCursor.fetchone()
	    if not item:
                item = "banana"
	    self.resp = re.sub(r"\$item", item, self.resp, 1)
        while "$newitem" in self.resp:
            self.logger.debug("Found $newitem in response - doing substitution")
            command.Command.databaseCursor.execute('SELECT noun FROM nouns ORDER BY RANDOM() LIMIT 1')
            item = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
            numItems = command.Command.databaseCursor.execute('SELECT count(*) FROM items')
            numItems = command.Command.databaseCursor.fetchone()[0]
            self.logger.debug("Found {0} items in bucket.".format(numItems))
            command.Command.databaseCursor.execute('SELECT item FROM items WHERE item = "{0}"'.format(item))
            if command.Command.databaseCursor.fetchone():
                self.logger.debug("Item is already in bucket, not reinserting")
                continue
            elif numItems >= 15:
                self.logger.debug("More than 15 items found")
                command.Command.databaseCursor.execute('SELECT item FROM items ORDER BY RANDOM() LIMIT 1')
                itemToDrop = command.Command.databaseCursor.fetchone()[0].encode('ascii', 'ignore')
                self.logger.debug("Dropping item: {0}".format(itemToDrop))
                command.Command.databaseCursor.execute('DELETE FROM items WHERE item = "{0}"'.format(itemToDrop))
            self.logger.debug("Inserting {0} into bucket".format(item))
            command.Command.databaseCursor.execute('INSERT INTO items VALUES ( "{0}" )'.format(item))
            command.Command.database.commit()
            self.resp = re.sub(r"\$newitem", item, self.resp, 1)
        self.logger.info("Got final response: {0}".format(self.resp))
        return self.resp

