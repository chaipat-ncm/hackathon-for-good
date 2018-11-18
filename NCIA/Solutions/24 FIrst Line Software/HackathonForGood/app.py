import urllib.request
import os
import shutil
import tempfile
import assessment_service

from flask import Flask, request, render_template

index_path = 'index.html'

TEMP_DIRIMAGE_TO_ANALYZE = tempdir = tempfile.mkdtemp()

app = Flask(__name__)


@app.route('/', methods=['GET'])
def index():
    return render_template(index_path)


@app.route("/", methods=["POST"])
def process_image():
    uploadurl = request.form['url']
    filename = uploadurl[uploadurl.rfind("/") + 1:]

    save_image(filename, uploadurl)

    assessment = assessment_service.assess(TEMP_DIRIMAGE_TO_ANALYZE, filename)

    # analyze
    result = Response(filename, uploadurl, assessment)
    return render_template(index_path, results=[result])


def save_image(filename, uploadurl):
    shutil.rmtree(TEMP_DIRIMAGE_TO_ANALYZE)
    os.makedirs(TEMP_DIRIMAGE_TO_ANALYZE)
    imagepath = os.path.join(TEMP_DIRIMAGE_TO_ANALYZE, filename)
    urllib.request.urlretrieve(uploadurl, imagepath)


class Response:
    def __init__(self, imagename, imageurl,  chance):
        self.imagename = imagename
        self.imageurl = imageurl
        self.chance = chance


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(8080), debug=False)
