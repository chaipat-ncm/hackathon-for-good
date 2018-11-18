#!/usr/bin/env python3

import pandas as pd
import nltk
import io
import re
import os
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression

# Download stopwords and porter test
nltk.download('stopwords')
nltk.download('porter_test')
stop_words = nltk.corpus.stopwords.words('english')

ps = nltk.PorterStemmer()


# Text PreProcessing Function Creation
def pre_process(txt):
    z = re.sub("[^a-zA-Z]", " ", str(txt))
    z = re.sub(r'[^\w\d\s]', ' ', z)
    z = re.sub(r'\s+', ' ', z)
    z = re.sub(r'^\s+|\s+?$', '', z.lower())
    return ' '.join(ps.stem(term)
                    for term in z.split()
                    if term not in set(stop_words)
                    )


model_file_path = os.environ.get('PROPAGANDA_NLP_MODEL')
dataset = pd.read_csv(io.open(model_file_path, 'r', encoding='utf-8'), header=None)

train_documents = dataset[0]
train_labels = dataset[1]


processed = train_documents.apply(pre_process)

vectorizer = TfidfVectorizer(ngram_range=(1, 2))
X_ngrams = vectorizer.fit_transform(processed)

# Train/Test Split
X_train, X_test, y_train, y_test = train_test_split(X_ngrams, train_labels, test_size=0.2, stratify=train_labels)

# Running the Classsifier
model = LogisticRegression()
model.fit(X_train, y_train)


def preprocess_message(message):
    return vectorizer.transform([pre_process(message)])


def predict_percentage(message):
    return model.predict(preprocess_message(message))


def predict(message):
    if predict_percentage(message):
        return True  # neutral/good
    else:
        return False  # bad


# USAGE EXAMPLES:
# text = 'Kosntantin'
# binary_prediction = predict(text)
# probability_prediction = model.predict_proba(preprocess_message(text))
# print(text + ' : ' + str(binary_prediction))
# print(text + ' : ' + str(probability_prediction))
#
# text = 'NATO stop war!'
# binary_prediction = predict(text)
# probability_prediction = model.predict_proba(preprocess_message(text))
# print(text + ' : ' + str(binary_prediction))
# print(text + ' : ' + str(probability_prediction))