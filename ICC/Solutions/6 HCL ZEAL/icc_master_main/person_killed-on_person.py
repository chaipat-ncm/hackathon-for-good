# -*- coding: utf-8 -*-

from refo import Question, Star, Any, Plus

from iepy.extraction.rules import rule, Token, Pos

RELATION = "person_killed-on_date"

@rule(True)
def person_killed_on_date(Subject, Object):
    anything = Star(Any())
    regex = anything + Subject + anything + Token("killed") + anything + Token("on") + Object + anything
    return regex
	
@rule(True)
def on_date_person_killed(Subject, Object):
    anything = Star(Any())
    regex = anything + Token("on") + Object + Star(Pos(",")) + Subject + anything + Token("killed") + anything
    return regex
	
