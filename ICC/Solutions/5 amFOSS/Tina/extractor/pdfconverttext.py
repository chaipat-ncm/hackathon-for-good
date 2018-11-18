#!/usr/bin/env python

import PyPDF2
import sys
import textract
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
filename = sys.argv[1] 
pdfFileObj = open(filename,'rb')
pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
num_pages = pdfReader.numPages
count = 0
text = ""
while count < num_pages:
    pageObj = pdfReader.getPage(count)
    count +=1
    text += pageObj.extractText()
if text != "":
   text = text
else:
   text = textract.process(fileurl, method='tesseract', language='eng')
#tokens = word_tokenize(text)
#punctuations = ['(',')',';',':','[',']',',']
#stop_words = stopwords.words('english')
#keywords = [word for word in tokens if not word in stop_words and not word in punctuations]
print (text)
