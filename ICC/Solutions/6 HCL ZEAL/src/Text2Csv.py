import uuid
import os
import csv

def text_2_csv(path):

        files = os.listdir(path)  # Get all the files in that directory
        print("Files in '%s': %s" % (path, files))
        totalFiles = len(files)
        OUTPUT_DIR =  'C:/ws/hfg/code/zeal/src/data/text2csv/'
        if not os.path.isdir(OUTPUT_DIR):
            os.mkdir(OUTPUT_DIR)
        csvFileName = files[0] + '.csv'
        csvFullFileName = os.path.join(OUTPUT_DIR, csvFileName)
        # csvFile = open(csvFullFileName, 'w+', encoding='utf-8')

        with open(csvFullFileName, 'w',  encoding='utf-8') as csvfile:
            fieldnames = ['document_id', 'document_text']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            count = 0
            writer.writeheader()
            for i in range(totalFiles):
                textFileName = path + files[i]
                fileContent  = open(textFileName,  encoding='utf-8').read().encode('UTF-8')
                print(textFileName)
                id = str(uuid.uuid4())
                count = count + 1
                writer.writerow({'document_id': id, 'document_text': fileContent})
        print(count)

if __name__ == '__main__':
    path = 'data/pdf2text/'
    text_2_csv(path)

