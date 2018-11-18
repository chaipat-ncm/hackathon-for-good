import PyPDF2
import textract
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
filename = "CR2018_05238.PDF"
pdfFileObj = open(filename,'rb')
pdfReader = PyPDF2.PdfFileReader(pdfFileObj, strict=False)
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
tokens = word_tokenize(text)
punctuations = ['(',')',';',':','[',']',',']
stop_words = stopwords.words('english')
keywords = [word for word in tokens if not word in stop_words and not word in punctuations]
print "Tokens"
print tokens
print "stop_words"
print stop_words
print "keywords"
print keywords
