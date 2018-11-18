#!/usr/bin/env python

import sys

from docx import Document

document = Document(sys.argv[1])

newfile = open(sys.argv[2], 'w')

newparatextlist = []

for para in document.paragraphs:
    newparatextlist.append(para.text)
    print(newparatextlist)
    newfile.write('\n\n'.join(newparatextlist))

