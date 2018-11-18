#!/usr/bin/env python

import PyPDF2
import sys
import os
import textract
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from docx import Document
from importlib import reload
import html2text

input_dir = sys.argv[1]

files = []
title  = "Flag{Title:"

for i in os.listdir(input_dir):
    thefile = os.path.join(input_dir, i)
    if i.endswith('.docx'):
        document = Document(thefile)
        w = open('output.txt', "a")
        
        newparatextlist = []

        for para in document.paragraphs:
            newparatextlist.append(para.text)
        string = ''.join(str(e) for e in newparatextlist)
        w.write(str(title) + str(i) + "}," + str(string))
        w.close()

    elif i.endswith('.html'):
        html = open(input_dir + '/' + i)
        f = html.read()
            
        w = open('output.txt', "a")
        
        h_parser = html2text.HTML2Text()

        h_parser.ingnore_links = True
        h_parser.ignore_anchors = True
        h_parser.inline_links = False
        h_parser.ingnore_emphasis = True
        h_parser.ignore_links = True

        text = h_parser.handle(f)
        text = text.strip(' \t\n\r')
        text = text.replace('\\n', '')
        w.write(str(title) + str(i) + "}," + str(text))
        w.close()

    elif i.endswith('.pdf'):
        count = 1
        pdfFileObj = open(thefile,'rb')
        pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
        num_pages = pdfReader.numPages
        count = 0
        text = ""
        
        w = open('output.txt', "a")
        
        while count < num_pages:
            pageObj = pdfReader.getPage(count)
            count +=1
            text += pageObj.extractText()
        if text != "":
            text = text
        else:
            text = textract.process(thefile, method='tesseract', language='eng')
        count = count + 1
        w.write(str(title) + str(i) + "}," + str(text))
        w.close()
