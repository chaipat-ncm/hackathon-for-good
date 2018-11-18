## Retrieve arabic-language news articles from webhose.io free API
## Jasper Ginn
## Hackathon for Good - ICC project
## Team: Peace Innovation Lab

## ------------------------------------------------------------------------------------------------------
##
## DESCRIPTION:
##
##   This script uses the free webhose.io API [https://webhose.io/dashboard] to download arabic-language
##   news articles that were scraped in the past 30 days. These articles can be filtered for specific
##   keywords (like kill, murder etc.). The keywords used to filter articles can be found in the
##   'terms_ss.csv' file (in the same folder as this script).
##
## INSTRUCTIONS:
##
##   Create an 'output' folder in the same folder as this script. Run it in PyCharm or via the terminal
##
## ------------------------------------------------------------------------------------------------------

import webhoseio as whq
from datetime import datetime, timedelta
import pandas as pd

class articleImporter:

    def __init__(self, language: str, keywords: list, max_results = 1000, source = None):

        '''
        Initialize object of class 'articleImporter'. Arguments specify language, source, max results, keywords to use
        when downloading webhose news articles. Note that this script does not check if any of your inputs are valid

        :param language: reference language for news articles to download
        :param max_results: maximum number of results to download. defaults to 1.000
        :param keywords: keywords for which to filter webhose text
        :param source: use only news articles from this source. Defaults to None
        '''

        self._language = language
        self._max_results = max_results
        self._keywords = keywords,
        self._source = source

        self._ts = int((datetime.today() - timedelta(days=30)).timestamp())
        self._sort = "crawled"

        self._query = construct_query(language, keywords, self._ts, self._sort, source)

    def retrieve_articles(self):

        '''
        Retrieve articles from webhose
        :return: dict with articles, stripped from everything but body text
        '''

        # Set token
        whq.config(token="<YOUR-KEY-HERE>")
        # Query data
        res = []
        iterator = 0

        # Fetch results
        output = whq.query("filterWebContent", self._query)
        # Add to iterator
        iterator = iterator + len(output['posts'])
        print(iterator)
        # Append to resultset
        res.append(output)

        # Process articles
        for unit in output['posts']:

            process_articles(unit)
            print(unit)

        print("Found {} results".format(output["totalResults"]))
        # Get next output
        while(iterator < self._max_results and output['moreResultsAvailable'] > 0):

            print("Making request")

            output = whq.get_next()
            iterator = iterator + len(output["posts"])
            #print(output)

            # Process articles
            for unit in output['posts']:
                process_articles(unit)
                print(unit)

            res.append(output)

        return(res)

## -----------------------
##
## Utilities
##
## -----------------------

def parse_keywords(keywords: list):

    '''
    Take a list of inputs and put them in the right format for webhose

    :param keywords: keywords to filter webhose news articles for
    :return: string value containing keywords argument for query string
    '''

    ## Place quotes around each keyword
    keywords = ["({})".format(keyword) for keyword in keywords]
    kw_str = " OR ".join(keywords)
    kwq = "text:({})".format(kw_str)

    # Return
    return(kwq)

def construct_query(language: str, keywords: list, timestamp, sort, source = None):

    '''

    Take langauge, keywords, timestamp, sort parameters and turns it into a valid query to be used with webhose python package
    :param language: language of articles to be queried
    :param keywords: list of keywords that webhose should filter for
    :param timestamp: the free API can go back max 30 days for articles. The program sets this date automatically.
    :param sort: how are results sorted? We only use by date scraped.
    :param source: specific news outlet (e.g. cnn.com, bbc.co.uk, aljazeera.net)
    :return: dict containing 'q' (query), 'ts' (timestamp) and source
    '''

    # parse keywords
    kwstr = parse_keywords(keywords)

    # construct query
    if source is not None:
       q = "language:{} site_type:news site:{} {}".format(language, source, kwstr)
    else:
       q = "language:{} site_type:news {}".format(language, kwstr)

    # Construct dict
    res = {
        "q":q,
        "ts":timestamp,
        "sort":sort
    }

    return(res)

def process_articles(article):

    '''
    Take an article from webhose results and write it to a plain text file

    :param article: dict file, single result from webhose API query
    :return: Nothing.
    '''

    uid = article['thread']['uuid']
    body = article['text']

    # Write text to file
    with open('output/{}.txt'.format(uid), 'w', encoding='utf-8') as outFile:
        outFile.write(body)

##------------------------
##
## Call main
##
##------------------------

if __name__=="__main__":

    ## Ingest csv with arabic words
    kw = pd.read_csv("data/terms_ss.csv")

    ## Process csv
    kwlist = kw["term"].tolist()
    print(kwlist)
    print(len(kwlist))

    ## TODO: If list of kws too long, split into multiple lists
    kwlist=kwlist[66:]
    ai = articleImporter("arabic", kwlist, source="aljazeera.net", max_results=1000)
    print(ai._query)
    print(ai._ts)

    ## Retrieve news articles and write to file
    q = ai.retrieve_articles()


