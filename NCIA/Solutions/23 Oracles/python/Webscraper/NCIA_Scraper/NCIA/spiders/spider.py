from scrapy.spider import BaseSpider
import lxml.etree
import lxml.html
import json
import re
from unidecode import unidecode
from pprint import pprint
import os
from os import listdir
from os.path import isfile, join
from nested_lookup import nested_lookup
from urlparse import urlparse
from urlparse import urlsplit

class NICA_Spider(BaseSpider):
    name = "ncia"
    
    allowed_domains = []
    start_urls = []
    filename = ''
    # ScraperOutput_path = 'ScraperOutput/'
     
    def __init__(self, filename=None):
        if filename:

            self.filename = filename.strip('URLS/').replace('_start_urls.txt', '_ScraperOutput')
            self.ScraperOutput_path = 'ScraperOutput/'

            
            files = [pos_json for pos_json in os.listdir(self.ScraperOutput_path) if pos_json.endswith('.json')]

            if self.filename not in files:
                print(self.filename)
                with open(os.path.join(self.ScraperOutput_path + self.filename +".json"), mode='w') as f:
                    json.dump({"name": self.filename, "type" : "image", "keywords":[], "urls" : []}, f)

            with open(filename, 'r') as f:
                self.start_urls = f.readlines()  
            for url in self.start_urls:
                self.allowed_domains.append("{0.scheme}://{0.netloc}/".format(urlsplit(url)))
                self.this_URL = self.allowed_domains.append("{0.scheme}://{0.netloc}/".format(urlsplit(url)))

    def parse(self, response):
        item = []
        # item['url'] = self.this_URL
        root = lxml.html.fromstring(response.body)

        # optionally remove tags that are not usually rendered in browsers
        # javascript, HTML/HEAD, comments, add the tag names you dont want at the end
        lxml.etree.strip_elements(root, lxml.etree.Comment, "script", "head")

        # complete text
        # print lxml.html.tostring(root, method="text", encoding=unicode)
        rawdata = unidecode(lxml.html.tostring(root, method="text", encoding=unicode))
        data = re.sub('\s+',' ',rawdata)
        # item[self.this_URL] = {data}
        
        # return data

        with open(os.path.join(self.ScraperOutput_path + self.filename+ ".json"), 'r') as f:
            d = json.load(f)

        d["urls"].append({'url': self.this_URL, 'text': data})


        print(d)

        with open(os.path.join(self.ScraperOutput_path + self.filename+ ".json"), mode='w') as feedsjson:
            feedsjson.write(json.dumps(d))


        # with open(os.path.join(self.ScraperOutput_path + self.filename+'.json'), 'w') as outfile:
        #     json.dump(data, outfile)
    