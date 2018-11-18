from PyPDF2 import PdfFileReader
import string
import random
import os

def get_info(path):
    with open(path, 'rb') as f:
        pdf = PdfFileReader(f)
        info = pdf.getDocumentInfo()
        number_of_pages = pdf.getNumPages()
    print(info)
    author = info.author
    creator = info.creator
    producer = info.producer
    subject = info.subject
    title = info.title

def text_extractor(path, pdfFileName):
    with open(path + pdfFileName, 'rb') as pdfFile:
        pdf = PdfFileReader(pdfFile)
        cwd = os.getcwd()  # Get the current working directory (cwd)
        files = os.listdir(cwd)  # Get all the files in that directory
        print("Files in '%s': %s" % (cwd, files))

        pdfInfo = pdf.getDocumentInfo()
        totalPages = pdf.getNumPages()
        # OUTPUT_DIR = cwd + '/data/pdf2text/' + ''.join(random.choice(string.ascii_lowercase + string.ascii_uppercase + string.digits) for _ in range(10))
        OUTPUT_DIR =  'C:/ws/hfg/code/zeal/src/data/pdf2text/'
        if not os.path.isdir(OUTPUT_DIR):
            os.mkdir(OUTPUT_DIR)
        # get the first page
        for i in range(totalPages):
            page = pdf.getPage(i)
            pdfFileTitle = ''
            if pdfInfo.title is None:
                pdfFileTitle = pdfFileName
            else:
                pdfFileTitle = pdfInfo.title
            fileName = str(i) + '_' + pdfFileTitle + '_' + 'PDF' + '.txt'
            textFile = open(os.path.join(OUTPUT_DIR, fileName), 'w+', encoding='utf-8')
            textFile.write(page.extractText().replace("\n", ""))
            textFile.close()
        pdfFile.close()
if __name__ == '__main__':
    path = '../sampleData/'
    files = os.listdir(path)  # Get all the files in that directory
    print("Files in '%s': %s" % (path, files))
    totalFiles = len(files)
    for i in range(totalFiles):
        text_extractor(path, files[i])

