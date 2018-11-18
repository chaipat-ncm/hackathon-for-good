import base64
import logging
import os
import sqlite3

from flask import Flask, json
from flask import request, Response

from FullChainHandler import FullChainHandler
from flask import abort

app = Flask(__name__)

verdict = "live"

@app.route('/')
def hello_world():
    return "Hello World"

@app.route('/image', methods = ['POST'])
def post_image():

    payload = json.loads(request.data)

    filename = payload["filename"]
    vision_api_data = payload["vision"]

    vision_path = "%s/NCIA_Scraper/VisionAPI_Output/%s" % (os.getcwd(), verdict)
    FullChainHandler.urls_path = "%s/NCIA_Scraper/URLS" % os.getcwd()
    FullChainHandler.processing_path = "%s/../../processed/%s" % (os.getcwd(), verdict)
    FullChainHandler.cortical_path = "%s/../../semantic-maps/%s" % (os.getcwd(), verdict)
    FullChainHandler.logger = logging.getLogger("FullChainHandler")

    filepath = "%s/%s" % (vision_path, filename)
    with open(filepath, 'w') as f:
        f.write(json.dumps(vision_api_data))

    FullChainHandler.kick_off(filepath)

dir = "%s/../../.sqlite/" % os.getcwd()
cortical_path = "%s/../../semantic-maps/%s" % (os.getcwd(), "live")

@app.route('/results', methods=['GET'])
def get_results():
    filename = request.args.get("filename")

    db = sqlite3.connect("%s/results.db" % dir)

    res = db.execute("SELECT * FROM files inner join comparison on files.id = comparison.file1_id or files.id = "
                          "comparison.file2_id where files.name = '%s'" % filename).fetchall()
    db.close()
    if len(res) == 0:
        abort(400)

    cos_list = list(map(lambda x: json.loads(x[5])['cosineSimilarity'], res))
    av = sum(cos_list)/len(cos_list)

    with open("%s/%s" % (cortical_path, filename), 'rb') as f:
        image_dat = f.readlines()

    # TODO: calculate cosine average similarity with respect to good and bad images

    encoded_image = base64.b64encode(image_dat)

    return Response(response=json.dumps({"id" : filename, "processed": True, "semanticMapImage" : encoded_image}),
                    status=200,
                    mimetype='application/json')





if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
