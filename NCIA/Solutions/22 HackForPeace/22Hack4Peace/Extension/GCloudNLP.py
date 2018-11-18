
import googleapiclient.discovery
import six
import os
import pandas as pd
import re


def analyze_sentiment(text, encoding='UTF32'):
    
    body = {
        'document': {
            'type': 'PLAIN_TEXT',
            'content': text,
        },
        'encoding_type': encoding
    }

    try:
    	service = googleapiclient.discovery.build('language', 'v1')
    	request = service.documents().analyzeSentiment(body=body)
    	response = request.execute()
    	return response
    
    except:
    	return 0
    
def getfeatures_link(x):
	x = (str(x))
	best_search_label = eval(x)['Best guess label'].split(' ')
	top_websites = eval(x)['10 websites'] 
	return (best_search_label,top_websites)


def getfeatures_logos(x):
	list_logos = []
	try:
		if(str(x)!='nan'):
			for p in (x.split(',')):
				if len(p)>0:
					list_logos.append(p)
			return(list_logos)
		else:
			return None
	except:
		return None


def getfeatures_spoof(x):
	try:
		if(str(x))!='nan':
			tag_string = (eval(x)[0])
			return (re.findall('spoofed: (.+?),',tag_string))
		else:
			return None
	except:
		return None

def getfeatures_text(x):

	try:
		#print 'Getting Features....'
		if(str(x))!='nan':
			sentiment_google= (analyze_sentiment(str(x).replace(',',' ')))
			#print sentiment_google
			if (sentiment_google!=0):
				document_sentiment = (sentiment_google['documentSentiment']['score'])
				sentence_sentiment = []
				for s in range(0,len(sentiment_google['sentences'])):
						sentence_sentiment_n = (sentiment_google['sentences'][s]['sentiment']['score'])
						sentence_sentiment.append(sentence_sentiment_n)
				#print ('.......')
				return document_sentiment,sentence_sentiment
			else:
				return None,None

		else:
			return None,None

	except:
		return None,None


def get_nlp_df(df_final):
	df_final['best_search_label'],df_final['top_websites'] = zip(*df_final['links'].apply(getfeatures_link))
	df_final['logos'] = df_final['logos'].apply(getfeatures_logos)
	df_final['spoofed'] = df_final['safe_search'].apply(getfeatures_spoof)
	df_final['document_sentiment'],df_final['sentence_sentiment'] = zip(*df_final['texts'].apply(getfeatures_text))
	df_for_testing = df_final[['img_id','best_search_label','top_websites','spoofed','document_sentiment','sentence_sentiment','logos']]
	return df_for_testing













