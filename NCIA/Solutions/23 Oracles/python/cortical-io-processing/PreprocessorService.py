import logging
from urlparse import urlsplit
import os

from flask import json
from nested_lookup import nested_lookup
from unidecode import unidecode


class PreprocessorService:

    def __init__(self, write_to_directory, vision_api_output_path):
        self.vision_api_output_path = vision_api_output_path
        self.write_to_directory = write_to_directory
        self.logger = logging.getLogger(PreprocessorService.__name__)

    def process(self):
        self.filename = os.path.basename(self.vision_api_output_path).replace(".json", "")
        with open(self.vision_api_output_path) as f:
            json_data = json.load(f)
        urls = nested_lookup('url', json_data)
        start_urls_i = []
        allowed_domains_i = []

        for url in urls:
            start_urls_i.append(url.encode("utf-8"))
            allowed_domains_i.append("{0.scheme}://{0.netloc}/".format(urlsplit(url)))

        start_urls = list(
            set(start_urls_i))

        start_urls = [x for x in start_urls if ".jpg" not in x]
        start_urls = [x for x in start_urls if ".php" not in x]
        start_urls = [x for x in start_urls if ".png" not in x]
        start_urls = [x for x in start_urls if ".jpeg" not in x]
        start_urls = [x for x in start_urls if ".txt" not in x]
        start_urls = [x for x in start_urls if ".pdf" not in x]
        start_urls = [x for x in start_urls if ".gif" not in x]

        with open("%s/%s.txt" % (self.write_to_directory, self.filename), 'w') as f:
            f.write("\n".join(start_urls))

        # allowed_domains = list(set(allowed_domains_i))
        #
        # with open("%s/%s_allowed_domains.txt" % (self.write_to_directory, self.filename), 'w') as f:
        #     f.writelines(allowed_domains)

        raw_Descriptions = nested_lookup('description', json_data)
        img_Descriptions = []

        for i in raw_Descriptions:
            img_Descriptions.append(unidecode(i))

        self.output_dir = "%s/NCIA_Scraper/ScraperOutput" % os.getcwd()
        with open("%s/%s.json" % (self.output_dir, self.filename), mode='w') as f:
            json.dump({"name": self.filename, "type" : "image", "keywords":img_Descriptions, "urls" : []}, f)
