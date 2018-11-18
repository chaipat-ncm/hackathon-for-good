import base64
import json
import logging
import os

import retinasdk


class CorticalService:

    def __init__(self, write_to_directory, read_from_path):
        self.write_to_directory = write_to_directory
        self.read_from_path = read_from_path
        self.logger = logging.getLogger(CorticalService.__name__)
        self.fullClient = retinasdk.FullClient("47cba140-ea54-11e8-bb65-69ed2d3c7927",
                                          apiServer="http://api.cortical.io/rest",
                                          retinaName="en_associative")

    def process_file(self):
        self.__extract_write_to_path()
        if self.__file_exists():
            self.logger.info("%s processed earlier" % self.read_from_path)
            return
        data = self.__open_json()
        self.__extract_text_keywords(data)
        self.__create_semantic_map(data)

    def __extract_text_keywords(self, data):
        self.__extract_keywords(data)
        with open("%s.json" % self.write_to_path, 'w') as file:
            file.write(json.dumps(data))

    def __create_semantic_map(self, json_data):
        l = list()
        l = l + json_data["keywords"]
        for entry in json_data["urls"]:
            l = l + entry["keywords"]
        self.__extract_and_store_image(" ".join(l))

    def __extract_image_file_name(self):
        self.filename_to = os.path.basename(self.read_from_path).replace(".json", "")

    def __extract_write_to_path(self):
        self.__extract_image_file_name()
        self.write_to_path = '%s/%s' % (self.write_to_directory, self.filename_to)

    def __file_exists(self):
        return os.path.exists(self.write_to_path)

    def __open_json(self):
        try:
            with open(self.read_from_path) as f:
                dat = json.load(f)

            if not self.__validate_json(dat):
                raise Exception("file is not a valid json file: %s" % self.read_from_path)
            return dat
        except Exception, e:
            self.logger.error("file is not a valid json file: %s" % self.read_from_path)
            raise Exception(e)

    def __validate_json(self, json_data):
        return self.__validate_json_object(json_data, "name") & \
            self.__validate_json_object(json_data, "type") & \
            self.__validate_json_entry_urls(json_data) & \
            self.__validate_json_keywords(json_data)

    def __validate_json_entry_urls(self, json_data):
        b = self.__validate_is_json_list(json_data, "urls")
        if not b:
            return b
        for entry in json_data["urls"]:
            b = b & self.__validate_url_struct(entry)

        return b

    def __validate_json_keywords(self, json_data):
        return self.__validate_is_json_list(json_data["keywords"])

    def __validate_url_struct(self, json_entry):
        return self.__validate_json_object(json_entry, "url") \
                & self.__validate_json_object(json_entry, "text") \
                # & self.__validate_json_keywords(entry)

    def __extract_and_store_image(self, keywords_string):
        imageData = self.fullClient.getImage(json.dumps({"text": keywords_string}), plotShape="square")
        imgdata = base64.b64decode(imageData)
        with open("%s.jpg" % self.write_to_path, 'wb') as file:
            file.write(imgdata)

    def __validate_json_object(self, json_object, attribute):
        try:
            v = json_object[attribute]
            if (type(v) == list) or (type(v) == set) | (type(v) == dict):
                self.logger.error(
                    "attribute '%s' is list but should be object in file %s" % (attribute, os.path.basename(
                        self.read_from_path)))
                return False
            return True
        except KeyError:
            self.logger.error(
                "Missing attribute '%s' in json of file %s" % (attribute, os.path.basename(self.read_from_path)))
            return False

    def __validate_is_json_list(self, json_object, attribute=None):
        try:
            if attribute == None:
                v = json_object
            else:
                v = json_object[attribute]
            if type(v) != list:
                self.logger.error(
                    "attribute '%s' should be a list but is not in file %s" % (attribute, os.path.basename(
                        self.read_from_path)))
                return False
            return True
        except KeyError:
            self.logger.error(
                "Missing attribute '%s' in json of file %s" % (attribute, os.path.basename(self.read_from_path)))
            return False

    def __extract_keywords(self, json_data):
        for url_entry in json_data["urls"]:
            text = url_entry["text"].replace(" and ", "").replace(" or ", "").replace(" the ", "").replace(" a ",
                                                                                                           "").replace(" on ", "")
            url_entry["keywords"] = self.fullClient.getKeywordsForText(text)