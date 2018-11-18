import json
import os
import re
from urlparse import urlsplit

import lxml.etree
import lxml.html
from scrapy.spider import BaseSpider
from unidecode import unidecode


class NICA_Spider(BaseSpider):
    name = "ncia"
    
    allowed_domains = []
    start_urls = []
    filename = ''
     
    def __init__(self, filename):

        self.filepath = filename
        self.filename = os.path.basename(filename).replace(".txt","")
        self.output_dir = "%s/ScraperOutput" % os.getcwd()

        with open(self.filepath, 'r') as f:
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

        with open("%s/%s.json" % (self.output_dir, self.filename), 'r') as f:
            d = json.load(f)

        d["urls"].append({'url': self.this_URL, 'text': data})


        print(os.path.join(self.output_dir, self.filename+ ".json"))

        with open("%s/%s.json" % (self.output_dir, self.filename), mode='w') as feedsjson:
            feedsjson.write(json.dumps(d))


        # with open("%s/%s.json" % (self.output_dir, self.filename), 'w') as outfile:
        #     json.dump(data, outfile)
    