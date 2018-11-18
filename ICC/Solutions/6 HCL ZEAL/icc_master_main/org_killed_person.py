# -*- coding: utf-8 -*-

from refo import Question, Star, Any, Plus

from iepy.extraction.rules import rule, Token, Pos

RELATION = "org_killed_person"

@rule(True)
def org_killed_person(Subject, Object):
    anything = Star(Any())
    regex = anything + Subject + anything + Token("killed") + Star(Pos("DT")) + Object + anything
    return regex
	
