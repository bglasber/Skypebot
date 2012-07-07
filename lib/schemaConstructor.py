import sqlite3
import logging

class SchemaConstructor:

    def __init__(self, database):
        """Construct and create the initial connections"""
        self.db = sqlite3.connect(database)
        self.c = self.db.cursor()
        self.logger = logging.getLogger('SchemaConstructor')
        

    def constructSchema(self):
        """constructSchema: construct the schema."""

        c = self.db.cursor()
        self.logger.debug("Creating tables that don't exist")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='responses'")
        if not c.fetchone():
            self.logger.info("Creating the responses tablex...")
            c.execute("CREATE TABLE responses ( query text collate nocase, responses )")
            c.execute("CREATE INDEX responses_index ON responses ( query collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='quotes'")
        if not c.fetchone():
            self.logger.info("Creating the quotes table...")
            c.execute("CREATE TABLE quotes ( username text collate nocase, quote )")
            c.execute("CREATE INDEX quotes_index ON quotes ( quote collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='nouns'")
        if not c.fetchone():
            self.logger.info("Creating the nouns table...")
            c.execute("CREATE TABLE nouns ( noun text collate nocase )")
            c.execute("CREATE INDEX nouns_index ON nouns ( noun collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='verbs'")
        if not c.fetchone():
            self.logger.info("Creating the verbs table...")
            c.execute("CREATE TABLE verbs ( verb text collate nocase )")
            c.execute("CREATE INDEX verbs_index ON verbs ( verb collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='presentVerbs'")
        if not c.fetchone():
            self.logger.info("Creating the presentVerbs table...")
            c.execute("CREATE TABLE presentVerbs ( verb text collate nocase )")
            c.execute("CREATE INDEX presentVerbs_index ON presentVerbs ( verb collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pastVerbs'")
        if not c.fetchone():
            self.logger.info("Creating the pastVerbs table...")
            c.execute("CREATE TABLE pastVerbs ( verb text collate nocase )")
            c.execute("CREATE INDEX pastVerbs_index ON pastVerbs ( verb collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='ingVerbs'")
        if not c.fetchone():
            self.logger.info("Creating the ingVerbs table...")
            c.execute("CREATE TABLE ingVerbs ( verb text collate nocase )")
            c.execute("CREATE INDEX ingVerbs_index ON ingVerbs ( verb collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='adjectives'")
        if not c.fetchone():
            self.logger.info("Creating the adjectives table...")
            c.execute("CREATE TABLE adjectives ( adjective text collate nocase )")
            c.execute("CREATE INDEX adjectives_index ON adjectives( adjective collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='pluralNouns'")
        if not c.fetchone():
            self.logger.info("Creating the pluralNouns table...")
            c.execute("CREATE TABLE pluralNouns ( noun text collate nocase )")
            c.execute("CREATE INDEX pluralNouns_index ON pluralNouns ( noun collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='places'")
        if not c.fetchone():
            self.logger.info("Creating the places table...")
            c.execute("CREATE TABLE places ( place text collate nocase )")
            c.execute("CREATE INDEX places_index ON places ( place collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='items'")
        if not c.fetchone():
            self.logger.info("Creating the items table...")
            c.execute("CREATE TABLE items ( item text collate nocase )")
            c.execute("CREATE INDEX items_index ON items ( item collate nocase )")
        c.execute("SELECT * FROM sqlite_master WHERE type='table' AND name='band_names'")
        if not c.fetchone():
            self.logger.info("Creating the band table...")
            c.execute("CREATE TABLE band_names ( name text collate nocase )")
            c.execute("CREATE INDEX band_names_index ON band_names ( name collate nocase )")
        self.logger.debug("Finished checking for tables")
        self.db.commit()


    def close(self):
        """close the db connection after constructing the schema"""
        self.db.close()



