
def give_first(s):
    return s.split(",")[0]

import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer

from sklearn.preprocessing import MinMaxScaler
import xgboost as xgb
df=pd.read_pickle('df_for_ML.pkl')


def preprocess(df):
    df=df.iloc[1:,:]
    df.set_index('img_id')
    df['logos']=df['logos'].astype(str)
    df['logos']=df['logos'].astype(str).str.strip("[")
    df['logos']=df['logos'].astype(str).str.strip("]")
    df['logos']=df['logos'].apply(lambda x: give_first(x))
    df['logos']=df['logos'].astype(str).str.strip("'")

    # df['logos']=df['logos'].astype(str).str.replace('\[|\]|\'', '')
    df['logos']=df['logos'].replace('', 'No_logo')

    df['logos']=df['logos'].replace('None', 'No_logo')
    df['logos']=df['logos'].replace('NATO Umbrella', 'NATO')
    df['logos']=df['logos'].replace('North Atlantic Treaty Organization (NATO)', 'NATO')

    df['logos']=df['logos'].replace('Russia Today', 'Russia')
    df['logos']=df['logos'].replace('Russian Towers', 'Russia')


    df['document_sentiment'].fillna(-.1, inplace=True)
    df['sentence_sentiment'].fillna(-.1,inplace=True)
    df['spoofed'].fillna('[POSSIBLE]',inplace=True)
    df['spoofed']=df.spoofed.astype(str).str.replace('\[|\]|\'', '')

    one_hot_encoded= pd.get_dummies(df['spoofed'])
    del df['spoofed']
    df=pd.concat([df,one_hot_encoded],sort=False,axis=1)
    df['top_websites']=df['top_websites'].astype(str).str.replace('\[|\]|\'', '')
    df['best_search_label']=df['best_search_label'].astype(str).str.replace('\[|\]|\'', '')
    buf2=0
    buf1=0
    buf1_list=[]
    buf2_list=[]
    for k in list(df['sentence_sentiment']):
        if k:
            buf1=np.array(k).max()-np.array(k).min()
            buf2=np.array(k).min()
            buf1_list.append(buf1)
            buf2_list.append(buf2)
        else:
            buf1_list.append(0)
            buf2_list.append(0)
            continue
    df['Min_score']=buf2_list
    df['Diff_score']=buf1_list
    del df['sentence_sentiment']
    labels=np.array(df['best_search_label'].astype(str))
    X = vectorizer.fit_transform(labels)
    vectorizer.get_feature_names()
    X.toarray().mean(axis=1)
    df['Label_vect_mean']=X.toarray().mean(axis=1)
    df['Label_vect_sum']=X.toarray().sum(axis=1)
    ls1=[]
    for k in list(df['top_websites']):
        if k!="":
            ls1.append([i.split(".")[-1] for i in k.split(",")])
        else:
            ls1.append('')
    df['top_websites']=ls1
    URL=np.array(df['top_websites'].astype(str))
    X = vectorizer.fit_transform(URL)
    vectorizer.get_feature_names()
    X.toarray().mean(axis=1)
    df['top_websites_vect_mean']=X.toarray().mean(axis=1)
    df['top_websites_vect_sum']=X.toarray().sum(axis=1)
    del df['best_search_label']
    del df['top_websites']
    df_logos=df.copy()
    df_no=df.copy()
    df_logos['propoganda'] = 0
    y=df_logos['propoganda']
    X=df_logos.drop(['propoganda'],axis=1)
    one_hot_encoded_train = pd.get_dummies(X['logos'])
    df_logos=pd.concat([df_logos,one_hot_encoded_train],sort=False,axis=1)
    del df_logos['logos']
    if 'index' in df_no.columns:
        del df_no['index']
    del df_no['logos']
    del df_no['img_id']
    if 'propanda' in df_no.columns:
        y=df_no['propoganda']
        X=df_no.drop(['propoganda'],axis=1)
    return X,y


from sklearn.model_selection import train_test_split
import numpy as np
X, y = preprocess(df)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.30, random_state=42)
scaler = MinMaxScaler()
column_names=X_train.columns.values
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)
X_train=pd.DataFrame(data=X_train,columns=column_names)
X_test=pd.DataFrame(data=X_test,columns=column_names)


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
from sklearn.ensemble import ExtraTreesClassifier as ETC
forest = ETC(n_estimators=250, random_state=0)
forest.fit(X_train, y_train)
importances = forest.feature_importances_
std = np.std([tree.feature_importances_ for tree in forest.estimators_],axis=0)
indices = np.argsort(importances)
print('The accuracy=' + str(forest.score(X_test,y_test)))
f, ax = plt.subplots(figsize=(15,10))
plt.title("Feature importances")
plt.barh(range(X_train.shape[1]), importances[indices], xerr=std[indices], align="center")
plt.yticks(range(X_train.shape[1]), X_train.columns.values[indices])
plt.ylim([-1, X_train.shape[1]])
plt.show()


df_from_json =pd.read_csv('df_nlp.csv')
X,y = preprocess(df_from_json)


for col in X_test.columns:
    if(col in X.columns):
        print (col)
    else:
        X[col] = 0
        

X = X[['document_sentiment', 'LIKELY', 'POSSIBLE', 'UNKNOWN', 'UNLIKELY',
       'VERY_LIKELY', 'VERY_UNLIKELY', 'Min_score', 'Diff_score',
       'Label_vect_mean', 'Label_vect_sum', 'top_websites_vect_mean',
       'top_websites_vect_sum']]
X_test_new = scaler.transform(X)


ypred = forest.predict(X_test_new)

ypred

