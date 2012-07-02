import command
class WordHandler:    

	validCommands = [ "verb+",      "verb-",
			"verbed+",    "verbed-",
			"verbing+",   "verbing-",
			"verbs+",     "verbs-",
			"noun+",      "noun-",
			"nouns+",     "nouns-",
			"adjective+", "adjective-",
            "place+", "place-",
	]

	def __init__(self, parsedcommand, listOfTerms):

        	self.parsedcommand = parsedcommand
		self.listOfTerms = listOfTerms
        
	def isValidCommand(self):
		if self.parsedcommand in WordHandler.validCommands:
			return True
		else:
			return False
	def writeChanges(self):
		try:
			if self.parsedcommand == "verb+":
				command.Command.databaseCursor.execute('''INSERT INTO verbs VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verb-":
				command.Command.databaseCursor.execute('''DELETE FROM verbs WHERE verb = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbs+":
				command.Command.databaseCursor.execute('''INSERT INTO presentVerbs VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbs-":
				command.Command.databaseCursor.execute('''DELETE FROM presentVerbs WHERE verb = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbing+":
				command.Command.databaseCursor.execute('''INSERT INTO ingVerbs VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbing-":
				command.Command.databaseCursor.execute('''DELETE FROM ingVerbs WHERE verb = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbed+":
				command.Command.databaseCursor.execute('''INSERT INTO pastVerbs VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "verbed-":
				command.Command.databaseCursor.execute('''DELETE FROM pastVerbs WHERED verb = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "noun+":
				command.Command.databaseCursor.execute('''INSERT INTO nouns VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "noun-":
				command.Command.databaseCursor.execute('''DELETE FROM nouns WHERE noun = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "nouns+":
				command.Command.databaseCursor.execute('''INSERT INTO pluralNouns VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "nouns-":
				command.Command.databaseCursor.execute('''DELETE FROM pluralNouns WHERE noun = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "adjective+":
				command.Command.databaseCursor.execute('''INSERT INTO adjectives VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
			elif self.parsedcommand == "adjective-":
				command.Command.databaseCursor.execute('''DELETE FROM adjectives WHERE adjective = "{0}"
							   '''.format(' '.join(self.listOfTerms)))
                        elif self.parsedcommand == "place+":
				command.Command.databaseCursor.execute('''INSERT INTO places VALUES ("{0}")
							   '''.format(' '.join(self.listOfTerms)))
                        elif self.parsedCommand == "place-":
                                command.Command.databaseCursor.execute('''DELETE FROM places WHERE place = "{0}"
							   '''.format(' '.join(self.listOfTerms)))

			command.Command.database.commit()
			if "+" in self.parsedcommand:
				return "Added successfully"
			else:
				return "Deleted successfully"

		except:
			return "Operation Failed!"



