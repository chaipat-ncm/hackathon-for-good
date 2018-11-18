import scrapy
from scrapy.crawler import CrawlerProcess
import json
import re
from pprint import pprint
import os
from os import listdir
from nested_lookup import nested_lookup
from urlparse import urlparse
from urlparse import urlsplit
from unidecode import unidecode

#########################################
# Load JSON File from Vision API Output #
#########################################

# PropagandaType = 'good/'
PropagandaType = 'bad/'

cwd = os.getcwd()
print(cwd)
visionAPI_Output_path = 'VisionAPI_Output/'+PropagandaType
URLs_path = 'URlS/'+PropagandaType

json_files = [pos_json for pos_json in os.listdir(visionAPI_Output_path) if pos_json.endswith('.json')]
print(json_files)  # for me this prints ['foo.json']

for file in json_files:
    print('new file')
    print
    print
    with open(os.path.join(visionAPI_Output_path, file)) as f:
        filename = file.strip('.json')
        parsed_data = json.load(f)
        # pprint(parsed_data)

    d = parsed_data
    print(filename)
    ############
    # Get URLs #
    ############

    urls = nested_lookup('url',d)
    start_urls_i = []
    allowed_domains_i = []

    for url in urls:
        start_urls_i.append(url.encode("utf-8"))
        allowed_domains_i.append("{0.scheme}://{0.netloc}/".format(urlsplit(url)))

    start_urls = list(
        set(start_urls_i))

    start_urls = [ x for x in start_urls if ".jpg" not in x ]
    start_urls = [ x for x in start_urls if ".php" not in x ]
    start_urls = [ x for x in start_urls if ".png" not in x ]
    print(cwd)
    
    with open(os.path.join(URLs_path+filename+'_'+PropagandaType.strip('/')+'_start_urls.txt'), 'w') as f:
            for item in start_urls:
                f.write("%s\n" % item)

    allowed_domains = list(set(allowed_domains_i))

    # with open(os.path.join(URLs_path+filename+'_allowed_domains.txt'), 'w') as f:
    #     for item in start_urls:
    #         f.write("%s\n" % item)

    print(filename)
    print
    print
    print("Start URLs")
    

    pprint(start_urls)
    print
    print
    print
    print("Allowed URLs")
    pprint(allowed_domains)
    print
    print
    print
    print
    print
    print
    print
    print

    ####################
    # Get Descriptions #
    ####################

    raw_Descriptions = nested_lookup('description',d)
    img_Descriptions = []

    for i in raw_Descriptions:
        img_Descriptions.append(unidecode(i))

    pprint(img_Descriptions)



