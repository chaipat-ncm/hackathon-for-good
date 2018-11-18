import pickle
import re
import pandas as pd

def color(string):
   #string = "fraction: 0.0444072820246\nr: 26.0\ng: 43.0\nb: 101.0\na:\nfraction: 0.120587140322\nr: 238.0\ng: 251.0\nb: 253.0\na:"
    fraction = re.findall('fraction: (.+?)\t',string)
    r = re.findall('r: (.+?)\t',string)
    g = re.findall('g: (.+?)\t',string)
    list_dict = []
    for i in range(0,len(fraction)):
        dict_arr = {'fraction':fraction[i],'r':r[i],'g':g[i]}
        list_dict.append(dict_arr)
    return list_dict

def parse_to_list(text):
    web_entities=[]
    best_label=""
    for line in text.split("\n"):
        if(line[0:4]=="Best"):
            best_label=line[len('Best guess label:')+1:]
    list_texts1 = re.findall('http://(.+?)/',text)
    list_texts2 = re.findall('http[a-z]://(.+?)/',text)
    list_texts=list_texts1+list_texts2
    dict_web={}
    return {'Best guess label':best_label, '10 websites':list_texts}

def create_dic(list_):
    dic={}
    for i in list_:
        if i not in dic.keys():
            dic[i]=1
        else:
            dic[i]=dic[i]+1
    return dic

def parse_my_df(data):
    data['links']=data['links'].str.strip("\n")
    data['links']=[parse_to_list(s) for s in list(data['links'])]
    data['colors']=[color(s) for s in list(data['colors'])]
    return data