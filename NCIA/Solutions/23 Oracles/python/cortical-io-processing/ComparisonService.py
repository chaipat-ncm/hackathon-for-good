import logging
import os
import re
import sqlite3
from glob import glob

import retinasdk
from flask import json


class ComparisonService:

    def __init__(self, read_from_dir):
        self.read_from_dir = read_from_dir
        self.logger = logging.getLogger(ComparisonService.__name__)
        self.fullClient = retinasdk.FullClient("47cba140-ea54-11e8-bb65-69ed2d3c7927",
                                          apiServer="http://api.cortical.io/rest",
                                          retinaName="en_associative")
        self.dir = "%s/../../.sqlite/" % os.getcwd()

    def process(self):
        files = [y for x in os.walk(self.read_from_dir) for y in glob(os.path.join(x[0], '*.json'))]

        self.__update_files_table(files)

        self.__create_comparisons()

    def __update_files_table(self, files):
        self.__extract_from_database()
        for file in files:
            data = self.__open_json(file)
            name = data["name"]
            if not self._results_in_db.has_key(name):
                keywords = self.__get_keywords(data)
                lastrowid = self.insert_into_db(name, keywords)
                self._results_in_db[name] = (lastrowid, name, keywords)

    def __create_comparisons(self):
        self.__extract_comparisons()


        for value in self._results_in_db.values():
            stored_file_ids = set(map(lambda x: x[0], self._results_in_db.values()))
            id = value[0]
            if id in [413, 437, 39, 415]:
                self.logger.debug("skipping file with id %s" % id)
                continue
            self.logger.debug("processing file with id %s" % id)
            if self.__comparisons.has_key(id):

                compared_file_ids = self.__comparisons[id]
                missing_ids = [x for x in stored_file_ids if x not in compared_file_ids]
            else:
                missing_ids = stored_file_ids
            if len(missing_ids) == 0:
                continue
            __for_bulk_cortical = set()
            keywords_one = value[2]

            if (keywords_one is None) or (keywords_one == ''):
                self.logger.error("Cannot process file with id %s, because there are no keywords" % id)
                continue
            for _id in missing_ids:

                name = filter(lambda row: row[0] == _id, self._results_in_db.values())[0][1]
                r = self._results_in_db[name]
                keyword_other = r[2]

                if (keyword_other is None) or (keyword_other == ''):
                    self.logger.error("Cannot process file with id %s, because there are no keywords" % _id)
                    continue
                if id>_id:
                    entry = ((_id, keyword_other), (id, keywords_one))
                else:
                    entry = ((id, keywords_one), (_id, keyword_other))

                __for_bulk_cortical.add(entry)

            __for_bulk_cortical = list(__for_bulk_cortical)
            body = list()
            for entry in __for_bulk_cortical:
                body.append([{"text": entry[0][1]},{"text": entry[1][1]}])

            comparison_result = self.fullClient.compareBulk(json.dumps(body))

            query = "INSERT INTO comparison VALUES "
            for index in range(len(body)):
                res = comparison_result[index]
                fbc_entry = __for_bulk_cortical[index]
                query = query + "(%s, %s, '%s')," % (fbc_entry[0][0], fbc_entry[1][0],
                    json.dumps({
                        "sizeLeft" : res.sizeLeft,
                        "cosineSimilarity" : res.cosineSimilarity,
                        "overlappingLeftRight" : res.overlappingLeftRight,
                        "overlappingRightLeft" : res.overlappingRightLeft,
                        "weightedScoring" : res.weightedScoring,
                        "euclideanDistance" : res.euclideanDistance,
                        "jaccardDistance" : res.jaccardDistance,
                        "overlappingAll" : res.overlappingAll,
                        "sizeRight" : res.sizeRight
                    }) )
            query = query[:-1]

            db = sqlite3.connect("%s/results.db" % self.dir)
            try:
                with db:
                    db.execute(query)
            except sqlite3.IntegrityError:
                self.logger.error("failed query execution %s" % query)
            db.close()


    def __get_keywords(self, json_data):
        l = list()
        l = l + json_data["keywords"]
        for entry in json_data["urls"]:
            l = l + entry["keywords"]
        combined  = ",".join(l)
        return combined.replace("'", "").replace(")","").replace("(","")


    def __extract_from_database(self):
        db = sqlite3.connect("%s/results.db" % self.dir)
        self._results_in_db = dict()
        for row in db.execute("SELECT * FROM files"):
            self._results_in_db[row[1]] = row
        db.close()

    def insert_into_db(self, name, keywords):
        db = sqlite3.connect("%s/results.db" % self.dir)

        query = "INSERT INTO files VALUES (null, '%s', '%s')" % (name, keywords)
        s = db.execute(query)
        db.commit()
        db.close()
        return s.lastrowid


    def __open_json(self, path):
        try:
            with open(path) as f:
                dat = json.load(f)

            # if not self.__validate_json(dat):
            #     raise Exception("file is not a valid json file: %s" % path)
            return dat
        except Exception, e:
            self.logger.error("file is not a valid json file: %s" % path)
            raise Exception(e)

    def __extract_comparisons(self):
        db = sqlite3.connect("%s/results.db" % self.dir)
        self.__comparisons = dict()
        for row in db.execute("SELECT * FROM comparison"):
            file1_id = row[0]
            file2_id = row[1]
            self.__fill_comparison_dict(file1_id, file2_id)
            self.__fill_comparison_dict(file2_id, file1_id)
        db.close()

    def __fill_comparison_dict(self, file1_id, file2_id):
        if file1_id == file2_id:
            return
        if not self.__comparisons.has_key(file1_id):
            self.__comparisons[file1_id] = [file2_id]
        else:
            self.__comparisons[file1_id].append(file2_id)


if __name__ == '__main__':
    FORMAT = "%(asctime)-15s %(message)s"
    logging.basicConfig(level=logging.DEBUG, format=FORMAT)

    read_dir = "%s/../../semantic-maps/" % (os.getcwd())
    comparison_service = ComparisonService(read_dir)
    comparison_service.process()