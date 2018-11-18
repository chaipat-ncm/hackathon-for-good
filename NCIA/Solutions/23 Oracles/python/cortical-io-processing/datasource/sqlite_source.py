

import sqlite3
import os
from glob import glob

dir = "%s/../../../.sqlite/" % os.getcwd()

db = sqlite3.connect("%s/results.db" % dir)


read_dir = "%s/../../../semantic-maps/bad" % (os.getcwd())

files = [os.path.basename(y).replace(".json", "") for x in os.walk(read_dir) for y in glob(os.path.join(x[0],
                                                                                                        '*.json'))]
query = "INSERT INTO comparison VALUES "
for file in files:

    r = db.execute("SELECT * FROM files WHERE files.name = '%s'" % file)
    r.fetchall()
    query = query + "(%s, 'bad')," % (r[0][0])
query = query[:-1]

db.execute(query)
db.commit()
db.close()

print files



# r = db.execute("SELECT * FROM files inner join comparison on files.id = comparison.file1_id or files.id = "
#                           "comparison.file2_id where files.id = 30")
#
# res = r.fetchall()
#
# print res


