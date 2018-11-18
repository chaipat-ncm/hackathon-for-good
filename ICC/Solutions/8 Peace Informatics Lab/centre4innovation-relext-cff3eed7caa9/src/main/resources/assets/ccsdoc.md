# Introduction #

ChitChat allows you to script information retrieval and conversations.

You can categorize texts by defining *labels* of interest with word patterns. 

The scripting language allows you to use those labels to automate dialogues, by defining *reply* rules. In other words, create chatbots.
        
The rest of this documentation is organized around those two type of rules. 
        
Gain insight in topics of conversations instantly, by leveraging your scripts to do information retrieval. Use this *analysis* to follow trends, or use this information to improve the conversations.
                           
                           
## A quick overview ##

ChitChat script helps you to classify a text with labels/tags by creating matching rules and lets you define feedback in terms of those labels.
A script is a list of rules that looks as follows:

```
@label1 <- expression1
@label2 <- expression2
```

for instance, when the rules 

```
@fruit <- (apple OR pear) AND NOT juice
@food <- bread OR rice OR @fruit
```

are evaluated against the text `Do you like rice?`, the `food` label will be matched.

When the same rule set is evaluated against `Apples is what I like`, both `food` and `fruit` are returned. Note that the `food` rule has a reference to another rule, namely `@fruit`.

In other words, this tool will efficiently scan for word patterns within a given text and assign the matching parts with a label. 

With the text classified in terms of your own labels, you know what a text is about. Fow example, when you would use this to classify text in a twitter stream, you see it enables you to monitor 'fruit' trends. This type of analysis has its own section in the application.  

Things become even more interesting when you use the matched labels to give relevant feedback to a text. This is done by defining replies that trigger in terms of your labels.

```
@fruit <- (apple OR pear) AND NOT juice

NOT @fruit -> Can you tell me about which type of fruit you like?
@fruit -> Mmmmmmm, I like to eat an @fruit too!
```  

When this script is used for an input text "I fancy an apple", a reply "Mmmmmmm, I like to eat an apple too!" is returned.
In case a text input doesn't match `@fruit`, it will ask you which fruit you like. 

By defining multiple labels and replies, you are making a chatbot. 

## Features ##
 
 - Internationalization : The rule set can in principle be used for any unicode character sequences. That means it can be used from English to Arabic. Language specific normalization can be plugged in, for more effective matching.
 - Named entity recognition: find classes of words, such as geographic locations
 - Emoticons: support for matching emoticons (such as :) or :smile:
 - Stateless: the logic for matching and chatting is stateless. No sessions need to be managed. This is good news for scaling this as a micro service.
 - Speed: The rules are compiled and optimized for efficient matching. Thousands of messages are easily processed per second. 

## Syntax ##

When writing any formal specification, you have to follow a strict format. Some notes:

 - If you want to spread your rule over different lines for readability, use some extra indentation for following lines.
```
@fruit <- apple & 
     orange 
     | pear
```
 - You can indent the entire rule as well. Make sure an empty line (double enter) is used to separate is from the previous statement, so it is not confused with a line continuation. 
 - Keywords and labels are case-sensitive.
 - Comments start with `#` and continue to the end of the line 
 - You can influence the way scripts are evaluated by putting certain options between curly brackets
```
@fruit {options} <- expression
``` 
In the following sections you'll encounter some of those options.
 
 - For those properties, single line YAML syntax is used.
 - If all rules share the same property, you can use a YAML 'front matter' section. See: Configuring text processing
  

# Label rules #

In summary there are the following ingredients assign labels by matching patterns:

 1. *word literals*: direct word occurrences that are either exactly or inexactly matched.
 2. *entity recognition*: special keywords that matches a certain class of words.
 3. *sequencing*: to define the importance of order of the words and keywords just define.
 4. *logical operators*: to define what occurrence of the above three ingredients make a rule trigger the assignment of a label.

## Label rule syntax ##

The general form  of a label rule is:
```
@myLabel {properties} <- match expression 
```

The first part `@myLabel` is the label/category/tag you want to define in terms of the match expression. The label should start with an `@`.

You can define `{properties}`, that modifies the behaviour when this rule is matched. 
Setting options is considered a bit more advanced, but it can be skipped. Then you would just write `@myLabel <- match expression`.

The following properties are available:

| keyword        | values | example | description |
| -------------- | ------ |---------| ----------- |
| `match` | `all` (default) or `first` | `{match: all}` | Within a sentence, do you want to retrieve all or only the first match? |
| `within` | `sentence` (default), `all` or NUMBER | `{within: 3}` | Determine the number of sentences the expression should match in. |
| `set` | `{key1: value1, ...}` | `{set: {mood: happy, score: 42}}` | Set properties for this label similar to the columns in a datasheet. |

The properties are explained in more detail later on.

## Why not just regular expressions ##

The nerd in you may wonder how this is different from using regular expressions.
Where regular expressions work on a character level, this pattern matcher works on a word level.
More high-level matching incentives can then be used:
 - finding stemmed/normalized occurrences, where `'juice'` = `'juicy'` = `'JUICES'`
 - finding word sequences with wildcards (where order is important): `the_?_apple` matches "the red apple", but not "the apple" or "the super red apple".
 - compose hierarchical rules, e.g. where `@fruit` in the `food` rule refers to the `fruit` expression
 
## Word literals ##

There are two types of word literals:

 1. Single quoted words are matched *in-exact*. That is: case insensitive, UTF normalized, accents and unusual characters are removed and words are stemmed. So, `'Cost-effective'` matches `'costeffectiveness'`.
 2. Double quoted words are matched *exact*. So, `"Cost-effective"` won't match `"cost-effective"` or `"Cost-effectiveness"` or `"costeffectiveness"`

You can omit the single quotes when words only consist of alpha-numeric character, so  `hello` equals `'hello'` 


## Sequences ##

In case you want to make sure multiple words are matched only when they are adjacent, the underscore can be used.

example:

`the_big_book` is matched in `He finds the BIG book nice`, but *not* in `The book is big`

Note that this is more specific than when using using a conjunction:

`the & big & book` is matched in `He finds the BIG book nice`, and *also* in  `The book is big`

Although the underscore operator is more expressive in how to match words (e.g. `"THE"_'monkey'`), a convenient shortcut is by using spaces in (double) quoted strings:
`"THE monkey"` or `'the ? monkey'`. Notice the latter variants have slightly different semantics.  


## Start/end location of sequences ##

If you want a sequence only to match at the start of a text, you can use the `TEXTSTART` keyword at the beginning of a sequence.

`TEXTSTART_hello` will match "hello there", but not "say hello".

Note: depending on the `scope` property discussed later, the `TEXTSTART` is either a sentence start or the start of the entire text.

In a similar way, `TEXTEND` can be appended to a sequence, to make sure the words only match at the end.

So, `red_apple_TEXTEND` will match "I eat the red apple", but not "The red apple is eaten by me". 



## Logical operators ##

Use logical expressions to define more fine grained queries. The following operators are defined.
 
 * `AND`, `&`: a conjunction of two words/expressions.
 * `OR`, `|`: a disjunction of two words/expressions.
 * `NOT`, `-`: a negation of a word/expression.
    
Parenthesis can be used to define/clarify evaluation order.

example:

`('Cost-effective' AND NOT free) OR cheap`

or, when you prefer using the operator symbols:

`('Cost-effective' & -free) | cheap`

### Wild cards ###

When using sequences, you can use wild-cards to skip a number of words, but make sure the order is taken into account.
  
* `?` matches a single word. `the_?_book` is matched in `He finds the BIG book nice`.
* `+` matches a one or more words. `the_+_book` is matched in `He finds the BIG book nice` and in `the very big book`, but not in `the book`.
* `*` matches a zero or more words. `the_*_book` is matched in `He finds the BIG book nice` and in `the very big book`, and also in `the book`.
 
 

## Named Entity Recognition ##

In some cases you want to match a class of words, and not define all possible forms in a rule.

The following keywords can be used:

| keyword        | description | example | properties |
| -------------- |-------------| ------- | ---------- |
| DATETIME | references to time instances | 'yesterday' or '6 PM' | now, timestamp |
| EMO_SMILE, EMO_CAKE, ... | specific emoticons/emoji in ASCII or unicode format| ':-)', '🍰', ':cake:' | emoticon |
| NUMBER | numbers in digits | '1', '2' | number |
| CARDINALNUMBER | numbers in words | 'one', 'two' | number |
| EMAIL | email address | 'f.bar@x.y.nl' | email |
| URL | web address | 'http://www.abc.xyz/123' | url |
| CITY | location names | 'Amsterdam' | normalname, latitude, longitude, population |
| FUZZYCITY | location names and alternative writings | 'AMS' | normalname, country, latitude, longitude, population |

The properties are extra information that are added to the match. For example:

```
@place <- CITY
```

will match like:

```
Come to MATCH{name=Khartoum, normalname=Khartoum, country=SD, latitude=15.55177, longitude=32.53241, population=1974647}{Khartoum} to join the party!
```

### Extension with CSV Data sheets ###

You can create custom data sheets, of which the first column is used to check word sequence occurrences, and the rest of the row is added as additional information to the match.
The CITY tag is implemented like this...

CITY.csv contains:
```
name	normalname	country	latitude	longitude	population
...
สกีดาม	Schiedam	NL	51.91917	4.38889	75438
斯希丹	Schiedam	NL	51.91917	4.38889	75438
Scheveningen	Scheveningen	NL	52.10461	4.27557	23000
Sheveningen	Scheveningen	NL	52.10461	4.27557	23000
Sjeveninge	Scheveningen	NL	52.10461	4.27557	23000
...
```

You can add your own data sheets in the same way. The keyword becomes the filename without extension. Per language you can have different datasheets.
The data sheets are loaded when starting the server application, if they are located in the right place. That is: `data/nlp/{language}/datasheet/{type}.csv` (e.g. `data/nlp/en/datasheet/CITY.csv`)


### Constraining property values ###

With entity recognition labels get assigned additional properties, like a numeric value for "five" (namely: 5) or cities get a latitude and longitude value.

Of course, the right words must be present in a text for a rule to match, but you can be even more strict on when a label should match by constraining property values.

For example:
```
@age <- (age OR year) AND NUMBER
@adult <- @age.number >= 18
```

will match `age` in  "I'm Mary and [14 year] old.", but not match `adult`.

It will both match `age` and `adult` in "I'm Mary and [70 year] old."

The comparison operators can be used to test different cases.
 
|         | *Operator*  |    `<` | `<=`                   |   `==`  | `!=`  |        `>=`  | `>`        |
|---------|---|--------|------------------------|---------|---------|--------------|------------|
| *Arguments*      | *Description*  | less than | less then or equals |  equals |  not equals | greater than | greater than or equals |
| number, number | Normal number comparison | `1 < 2` | `1 <= 2` | `3 == 3` |`3 != 4` | `4 >= 4` | `5 > 4` |
| number, string | Check string length      | `'hi' < 10` |`'hi' < 2` | `'hi' == 2` | `'hi' != 3` | `4 >= 'hi'` |`2 >= 'hi'` |
| string, string | Check substring (at start)     | `'at' < 'cats'` (anywhere) | `'ca' <= 'cats'` (at start) | `'cats' == 'cats'` | `'cats' != 'dog'` | `'cats' >= 'ca'` (at start) | `'cats' > 'at'` (anywhere) |

All examples in the table evaluate to true.

You can also use NER classes directly, instead of using the indirect `@age` rule:

```
@adult <- (age OR year) AND NUMBER.number >= 18
```

or 

```
@southern <- CITY.latitude < 0
```

## References ##

You can reuse a match rule by referencing to it from another rule.

```
@fruit <- (apple OR pear) AND NOT juice
@food <- bread OR rice OR @fruit
```

Note that the `food` rule has a reference to another rule, namely `@fruit`.

This means that food will also match in "I eat apples". 

It is good practice to break up your rules into sensible parts for performance, readability and flexibility reasons. 


# Reply rules #

Things become more interesting when you use the matches to give relevant feedback, instead of only labeling text as before. This is done by defining replies in, that are triggered under the condition that certain combinations of labels occur.


## Reply rule syntax ##

The general form  of a reply rule is:
```
expression {properties} -> reply messages
```

The `expression` is the condition that is checked, to see if the `reply messages` should be returned. The expression would normally reuse the labels defined before.

You can define `{properties}`, that modifies the behaviour when this rule is matched. 
Setting options is considered a bit more advanced, but it can be skipped. Then you would just write `@myLabel <- match expression`.

The following properties are available:

| keyword        | values | example | description |
| -------------- | ------ |---------| ----------- |
| `repeat` | - | `{repeat}` | Normally reply rules are used once. This property indicates it can be reused. |
| `continue` | - | `{continue}` | Normally the messages from one reply rule are returned. This indicates the next matching reply is returned as well. |
| `within` | `all` (default), `last`  or NUMBER | `{within: 3}` | Determine the number of messages back the expression should match in. |
| `addText` | `text` | `{addText: "important"}` | Appends the text to the next incoming message, thus labeling it. |
| `addLabel` | `label` or `[label1, label2, ...]` | `{addLabel: important}` | Tags the the next incoming message with the given label(s). |
| `removeLabel` | `label` or `[label1, label2, ...]` | `{removeLabel: important}` | If the given label has matched before and the reply expression matches, it is removed (effectively a reset). |
| `reflect` | - | `{reflect}` | Swaps personal and possesive pronouns in matched texts. |

The properties are explained in more detail later on.

Let's continue with an example:

```
@fruit <- (apple OR pear) AND NOT juice

-@fruit -> Can you tell me about which type of fruit you like?
@fruit -> Mmmmmmm @fruit!
```  

When this script is used for an input text "I fancy an apple", a reply "Mmmmmmm apple!"  
For another input, "Motorcycles are cool", "Can you tell me about which type of fruit you like?" would be triggered. 

You can use any logical expression to make a condition. E.g.

```
...
@foo              -> Use earlier defined labels
'hello'           -> Use literals
@foo & -@bar      -> Use logical operators
@foo.number > 100 -> Use property comparisons

```

The first reply that matches is used. So it is advised to first state the most specific replies that only match under the most strict conditions, followed by reply statements that are more likely to match. 

You can use a reply without condition as a fallback scenario: 

```
() -> I don't know what you mean
```  

## Reply scope ##

By default, all messages in a conversation are used to see what labels are triggered, that are in turn used to see what reply rule is matching.

In this way, more and more context is build, thus directing the conversation to a more specific goal.

In some cases, you may want a reply only to focus on the last *n* messages, instead of the whole conversation. In this way you can focus replies on more recent messages. Especially when the option `repeat` is used, this may become practical.

You do this by using this option

```
@foo {within: 2} -> You just mentioned @foo.text
``` 

Now, only `@foo` matches in the last 2 messages are considered for this rule to trigger.

The default is `{within: all}`.

A more subtle variant is when the reply should trigger on at least a part of the last message. In other words, there is an update from an incoming message that partly satisfies matches: 

```
@foo {within: update} -> You just mentioned @foo.text
``` 

  

## Randomized answers ##

To make feedback look more lively, multiple reply texts can be specified, and a random one will be picked.

```
@food -> Mmmmm, I like that. | Not my cup of tea
```
Will either return "Mmmmm, I like that." or "Not my cup of tea." chosen by chance.


## Buttons ##

If you want a multiple choice question instead of free text, you can render buttons.

A button is defined in the reply, and has the form `BUTTON(Text on the button, Value sent)`.


For example:

```
# always ask
@ask <- *

# answer cases 
Y {repeat, within: 1} -> Yessss
N {repeat, within: 1} -> Nooo

# Ask yes or no 
@ask {repeat} -> Do you like icecream? & BUTTON(Yes, Y) BUTTON(No, N)
```


## Using matched variables ##

A label can matched for different reasons. You can use the match in the reply by putting a `@` in front of the label.

```
@fruit <- (apple OR pear) AND NOT juice
@fruit -> Mmmmmmm @fruit!
```  

The reply can take the form "Mmmmmmm apple!" or "Mmmmmmm pear!" in this case.

Some entity recognition matchers, such as `CITY` add properties to the matched text. For example 
`population` will hold the number of inhabitats of that town.
You can show those properties with the `@label.property` syntax as shown below.

```
@place <- CITY

@place {repeat} -> There live @place.population people in @place
() {repeat} -> What is your favorite city?
```

## Constraining replies by property comparison ##

The condition of when a reply is triggered is normally a logical expression using references.

```
...
@fruit & -@drinks -> Healthy food!
```

You can filter replies even more by comparing property values that resulted from entity recognition

```
@place <- CITY
@place.latitude < 0 -> You're from the southern hemisphere 
```

Take a look at the table shown before, defining the different comparison operators. 



## Using matched wildcards ##
When you use wildcards, `?`, `*` or `+`, the matched text can be used with the `@label.wildcard1` variable in replies.

For example:

```
@need <- I_need_+
@need -> Why do you need @need.wildcard1?
```

It will give answers like: "Why do you need a fast car?"

If multiple wildcards are used, you can refer to them as `@label.wildcard2`, `@label.wildcard3` and so on.

For example:

```
@statement <- the_?_is_+
@statement -> Why is the @statement.wildcard1 so  @statement.wildcard2?
```

Can result in: "Why is the guy so super cool?"

## Reflecting answers ##
When reusing matched text, you may want to transform the text slightly, such that personal and posessive pronouns are inverted, in order to make a correct reply.
The `reflect` option does that.

```
@need <- I_need_+
@need {reflect} -> Why do you need @need.wildcard1?
```

When the user says: "I need you", it will reply "Why do you need me?" instead of "Why do you need you?".



## Multiple replies ##

You can trigger sending back multiple replies with the `&` operation.

```
() -> Hi! & How are you?
```  

Will trigger separate messages:

"Hi!"

and 

"How are you?"

## Repeat giving the same feedback ##

In a chat setting, replies are disgarded once they have triggered. It doesn't make sense to repeat everything, but instead replies defined later should get a chance.

But when asking for clear input, you may want to repeat the request until it satisfies. 

You can do this with the `repeat` option.

```
@animal <- cat | dog

-@animal {repeat} -> Tell me about your pet
() -> know I know...
```
  
## Refine when a reply triggers ##

Sometimes a reply is triggered more than intended. Especially with `repeat`. Earlier matches in a conversation trigger a reply, while you intended to focus more on information that was just provided by the user.

For this reason, you can define a reply is only used when the last message contained a relevant update (given the terms in the condition).

```
...

@animal & @fruit {within: update} -> You just mentioned something about animals or fruit.
```

The default is `{within: all}`.

## Continue with other replies ##

In some cases you'll want to give feedback to an incoming massage and trigger a new question.

Returning multiple messages was already possible with `&`. In combination with the `CONTINUE` keyword. The answers are
checked top-down again, to return another matching reply.

```
@animal <- cat | dog
@fruit <- apple | orange

-@animal -> Tell me about your pet
@animal {continue} -> I know about your pet 

-@fruit -> Tell me about your favorite fruit
@fruit {continue} -> I know about your favorite fruit

``` 
Note: in practice *continuing* is handy when you want to give information first, instead of asking a question. With *continue* it will jump to a relevant question/reply later  

## Rephrasing ##

You can define replies wit the same condition multiple times. 
This allows you to ask the same thing again, in a different form.

```
-@animal -> Tell me about your pet
-@animal -> In specific, I'd like to now if you have a cat or a dog
```

## Disambiguating answers ##

Sometimes answers will be unclear without the proper context. For example, a user may sends a number. If you want to know both the number of kids and his/her own age, '12' may be unclear.  

The `addText: ....)` option in a reply will annotate the next (future) answer, so you can disambiguate it when matching otherwise possibly similar answers.
   
```
# labels that both have a numeric answer 
@kids <- (NUMBER OR CARDINALNUMBER) AND kids
@age <- (NUMBER OR CARDINALNUMBER) AND (age OR year_old)

@age & @kids -> You have @kids.number children and are @age.number years old.
# By appending "age" to the next answer, 
# an input like "25" becomes "25 age" 
# which only matches for age here. Not for kids.  
-@age {repeat, addText: age} -> How old are you?
@age {continue} -> Already @age.number year old? Wow...

# Same trick when adding "kids"... 
-@kids {repeat, addText: kids} -> How many kids do you have?
@kids {continue} -> Good luck with your @kids.number little monsters

```

In a similar way `addLabel` option in a reply will annotate the next (future) answer with an extra label, independent of the content.

## Example ##

An example using multiple reply techniques:

```
@vehicle <- car | bike | train  #won't match in this example
@fruit -> pear | (apple & -of_my_eye) | orange

-@vehicle & -@fruit {continue} -> Hi! Tell me a bit about you... 
@vehicle & @fruit -> I know all about you now... & Ciao! 

-@vehicle {repeat} -> Tell me about how you travel
@vehicle {continue} -> I like a fast @vehicle

# Use the matched word for this rule
-@fruit -> Tell me about what you eat, choose APPLE or PEAR
-@fruit {repeat} -> Tell me about what you eat
@fruit -> I like @fruit too! | I hate @fruit, though!
# Fallback case if no rule was matched
() -> I don't understand what you mean...

```

## Prefix blocks ##

With larger scripts, you may encounter that a group of replies all only need to trigger under the same condition (the labels matched).

For example, in this part of the script:

```
...

@age.number > 24 & @likesStrawberry & @red -> Reply 1 
@age.number > 24 & @likesStrawberry & @blue -> Reply 2 
@age.number > 24 & @likesStrawberry & @green -> Reply 3

... 
```

The condition `@age.number > 24 & @likesStrawberry` is common amongst the reply expressions.

In order to repeat yourself, you can use prefix block syntax: a way to prepend the expressions within `{ }` with a given part.

It keeps your logic more readable:

```
...

@age.number > 24 & @likesStrawberry & {
  @red -> Reply 1 
  @blue -> Reply 2 
  @green -> Reply 3
}
... 
```

The indentation used in the block is not needed, but helps to visually group the block.

  

# Configuring text processing #
There are some variables that can be tweaked to customize how language processing is done. You can do this by setting options in YAML format as a first part of the script. 

[YAML](https://en.wikipedia.org/wiki/YAML) is a human-readable data serialization language. It is commonly used for configuration files, but could be used in many applications where data is being stored.

The normal script starts after the second "---" separator of the configuration block.

```

---
languages:
- "en"
normalizers:
- "default"
wordTokenizer: "matching"
sentenceSplitter: "simple"
optimizeRuleLogic: true
rule:
  reply: {}
  label: {}
---
# rules and replies start here
...

```

The options are listed below:

| Key |  Format | example | Description |
| --- | ------ | ------ | ----- |
| `languages` | [String] | [en, ar] | A list of languages can be specified. They will determine how words are normalized (especially stemmed). Use the lower-case two-letter code for the languages (See: [ISO 639 codes](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)). Also the option to normalize emoticons |
| `normalizers` | [String] | [default] | Defines how words are normalized, by providing a list of function names that should be applied. |
| `wordTokenizer` | String | matching | How words are selected/tokenized. Use "matching" or "splitting". |
| `sentenceSplitter` | String | simple | Defines how sentences are selected from a text.  |
| `optimizeRuleLogic` | Boolean | true | Choose whether or not the rule compilation step should optimize the logic for better matching performance. |
| `rule: {label: ...}` | YAML| {scope: all}| Common properties for all label rules. Can be overwritten per rule, though. |
| `rule: {reply: ...}` | YAML| {repeat, within: 2}| Common properties for all reply rules. Can be overwritten per rule, though. |

## Default rule options ##

You can specify certain options for label and reply rules.

```
# label definition
@foo {scope: all} <- foo
``` 
or
```
# reply definition
@foo {repeat} -> Hi there!
``` 

If all rules use the same properties, you don't have to repeat yourself. Just define the common properties as follows:

```

---
rule: 
  label: {scope: all}
  reply: {repeat}
---

@foo <- foo
@foo -> Hi there!
```





# Editor #

Although scripts can be written in any text editor (for example Notepad), the build-in web editor provides some nice features.

* When you make a mistake, it will show you where to fix it.
* It has syntax highlighting, so colors reflect the function or type of a word.
* It will highlight the matching brackets when your cursor is on '(' or ')', helping to write correct expressions.
* It has the following useful shortcuts (besides default copy/pasting etc):

| Keys | Action |
| ---- | ------ |
| `Ctrl`+`Space` | Code completion |
| `Ctrl`+`F` | Find text |
| `Ctrl`+`Shift`+`R` | Replace text |
| `Ctrl`+`D` | Delete the current line |
| `Ctrl`+`Down` | Duplicate the current line |
| `Ctrl`+`Home` | Go to the start of the script |
| `Ctrl`+`End` | Go to the end of the script |
| `Ctrl`+`[` | Indent more |
| `Ctrl`+`]` | Indent less |
| `Ctrl`+`/` | Toggle line comments |

On Macs use [Cmd] instead of [Ctrl].

# About #

Arvid Halma - Centre for Innovation - Leiden University