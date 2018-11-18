import csv
import pandas as pd
from nltk.tokenize import sent_tokenize, word_tokenize 
from nltk import conlltags2tree, tree2conlltags
from nltk import pos_tag , ne_chunk
filename = open("text.txt").read()

with open("text.txt") as f:
    content = f.readlines()

sents = [x.strip() for x in content] 

#sents = sent_tokenize(filename)

temp = ()
csv_data = []

count = 1
for sentence in sents:
    sent=word_tokenize(sentence)
    
    if len(temp)>0:
        sent = tuple(sent) + temp
        temp = () 
    # if the extracted line in PDF is less than 5 words
    if len(sent)<7:
        temp = () 
        temp = temp + tuple(sent)
        continue
    else: 
        sent  = tuple(sent)
        
    
    for enti in sent:
        tree = ne_chunk(pos_tag(sent))
        iob_tags = tree2conlltags(tree)
        
    for i in range(0,len(iob_tags)):
        iob_tags[i] = iob_tags[i] + tuple(str(count))
        iob_tags[i] = list(iob_tags[i])
    csv_data.append(iob_tags)
    
    count  = count + 1
    
my_df = pd.DataFrame(csv_data)
my_df.to_csv('out.csv', index=False, header=False)
print(my_df)
