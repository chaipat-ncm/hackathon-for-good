from VisionAPI import *
from ParsingDF import *
from GCloudNLP import *


# USE VISION API CUSTOMISED TO RETRIEVE IMAGE FEATURES USING GOOGLE API
ImageFolder_neg = r"/Users/parvathykrishnank/Desktop/Hack4Peace/Master/Data/nato_bad_propaganda/"
ImageFolder_neutral = r"/Users/parvathykrishnank/Desktop/Hack4Peace/Master/Data/nato_neutral_propaganda/"

# COMMENT THIS LINE AFTER GETTING THE FEATURES FROM A TEST DATASET

#get_image_features_from_folders(ImageFolder_neg,ImageFolder_neutral)

df_neg = pd.read_pickle('Processed/df_google_search_negative.pkl')
df_neutral = pd.read_pickle('Processed/df_google_search_neutral.pkl')

# PARSE THE DATAFRAME TO READ JSON RESULTS
print 'Parsing.....................'
df_neg_parsed = parse_my_df (df_neg)
df_neutral_parsed = parse_my_df (df_neutral)

print 'Analyzing sentiment.............'
# SENTIMENT ANALYSIS ON THE TEXT

Ultimate_neg_df = get_nlp_df(df_neg)
Ultimate_neutral_df = get_nlp_df(df_neutral)

Ultimate_neg_df.to_pickle('Processed/df_ultimate_neg.pkl')
Ultimate_neutral_df.to_pickle('Processed/df_ultimate_neutral.pkl')

df_neutral = pd.read_pickle('Processed/df_ultimate_neg.pkl')
df_positive = pd.read_pickle('Processed/df_ultimate_neutral.pkl')
df_neutral['propoganda'] = 1
df_positive['propoganda'] = 0

df_for_ML = pd.concat([df_neutral,df_positive]).reset_index()
df_for_ML.to_pickle('Processed/df_for_ML.pkl')