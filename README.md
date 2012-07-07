Skypebot
========

A simple skype bot used to interact with skype conversations. 
Based off of xkcd's bucket bot.

# Setup: #

Bucket is designed to be run on a linux box, though you should be able to run it
on windows. This isn't supported, so you may have to modify some code.

That said, it should be possible. 

For the simplest setup, just install Skype, python development headers, and
Skype4Py. 

This process will vary depending on your Linux distribution, but the instructions
are all pretty clear.

At this point, simply run skype, set the display name to Bucket, and run:
$ python botclient.py

It will prompt you to allow it access. Click ok, and remember it.
You're good to go!


# Communicating with Bucket #

### Simple Breakdown ###

Bucket is based off of a trigger system. Quite simply, when a trigger is said in
conversation, bucket with respond with a programmed response.

Triggers and responses are a many-to-many system. That is, one trigger can have
many different responses, and multiple triggers may have the same response.
However, it is recommmended that you have unique responses. 

### Adding Stuff ###

You can talk to bucket to add things to him.

To add a trigger, simply type:
> bucket, add "trigger" "response"

Now, anytime someone says trigger in chat, bucket will respond with "response."
Note that the trigger can actually be a substring of what is said.
Thus, if a person types "im a trigger of awesomeness" in a sentence, bucket will
also respond with "response."

### More Complex Responses ###

Bucket has many different variables to make his responses more varied.

To use these, simply include them in the response field of the trigger.
> bucket, add "trigger" "$someone's $noun"

Here is a list of all available variables:

* $someone - select someone in the chat
* $who - the person who triggered the response
* $noun - a singular noun (DO NOT USE "a" before the noun [ ironing board vs an ironing board ])
* $nouns - plural noun
* $verb - unconjugated verb (i.e. drive)
* $verbs - does something (i.e. drives)
* $verbing - "ing" verb (i.e. driving)
* $verbed  - "ed" verb (i.e. drove, fitted)
* $adjective - an adjective
* $item - Use an item from bucket's inventory
* $popitem - Use an item from bucket's inventory, and delete it afterwards
* $newitem - Grab an item from the $noun table, and add it to bucket's inventory

For each of the word types, we can add possible words to their respective tables.
> bucket, noun+ car

Or delete a word:
> bucket, verb- drive

Note that because the word types depend on having nouns/etc. in the database,
we cannot use $noun or anything like that if there are no entries in the database.

### Inventory ###

Bucket's inventory is kept in-memory, and so will deleted on bucket restart.
Bucket's inventory is potentially limitless, and while it can hold any word type,
it should only hold nouns.

Since bucket's inventory can hold singular nouns and plural nouns, the usage of the
$item variables is made more complex because the surrounding syntax should be ambiguous
to the quantity.

In addition to the above variables, you can give bucket an item directly by using:
> /me gives bucket item

As before, do not use "a/an/the/etc" for the item.

### Editing Bucket ###

You can find out what triggered the last bucket response by simply talking to him:
> bucket, what was that

Furthermore, you can delete the last response using:
> bucket, forget that
