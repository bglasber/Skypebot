Skypebot
========

A simple skype bot used to interact with skype conversations. Based off of xkcd's bucket bot.

API:
=========

Overview:

Bucket is designed to be set up on a Linux box with a skype Display name of Bucket. You can change this if you like,
but you are going to have to edit code to compensate. 

Bucket is based around the idea of triggers and responses. 
Anytime a trigger is said, bucket will respond with a random response that is linked to that trigger.

A trigger-response grouping is one to many. That is, a single trigger can have many different possible responses.


Adding Triggers:

In any chat that bucket is connected to, you can type:
bucket, add "trigger" "response"

Then any time trigger is said, bucket may use that response.
Note trigger simply needs to be a substring of what is said, so "this is a trigger" will also trigger
the response.

To that end - it is encouraged that you use fairly long triggers to avoid having bucket trigger on everything.

Forgetting Triggers:

To have bucket forget the line which he last said, simply type:
bucket, forget that

Finding out what the trigger was:

type:
bucket, what was that

Responses:

Note that responses can contain many different variables. E.g.
"/me gives $someone a $noun"

These will be arbitrary selected from databases of those words.

The possible variables are:
$someone - select someone in the chat
$who - the person who triggered the response
$noun - a singular noun (DO NOT USE "a" before the noun [ ironing board vs an ironing board ])
$nouns - plural noun
$verb - unconjugated verb (i.e. drive)
$verbs - does something (i.e. drives)
$verbing - "ing" verb (i.e. driving)
$verbed  - "ed" verb (i.e. drove, fitted)
$adjective - an adjective

To add a word to those databases, use:
bucket, "wordtype"+ thing to add
(i.e. bucket, noun+ car )


