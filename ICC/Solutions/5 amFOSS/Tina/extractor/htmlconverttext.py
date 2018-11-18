#!/usr/bin/env python

import sys
import html2text

html = open(sys.argv[1])
f = html.read()

w = open("out.txt", "w")

h_parser = html2text.HTML2Text()

h_parser.ignore_links = True
h_parser.ignore_anchors = True
h_parser.inline_links = False
h_parser.ignore_images = True
h_parser.ignore_emphasis = True
h_parser.ignore_links = True

text = h_parser.handle(f)
text = text.strip(' \t\n\r')
text = text.replace('\\n', '')

w.write(text)
html.close()
w.close()

