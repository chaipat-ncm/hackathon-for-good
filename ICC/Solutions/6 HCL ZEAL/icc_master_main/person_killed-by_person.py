# -*- coding: utf-8 -*-

from refo import Question, Star, Any, Plus

from iepy.extraction.rules import rule, Token, Pos

RELATION = "person_killed-by_person"

@rule(True)
def person_killed_by_person(Subject, Object):
    anything = Star(Any())
    regex = anything + Subject + anything + Token("killed by") + Star(Pos("DT")) + Object + anything
    return regex
	
