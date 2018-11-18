from google.cloud import vision
from google.cloud.vision import types

from io import BytesIO

import pprint

def get_labels(image):
	client = vision.ImageAnnotatorClient()
	labels_response = client.label_detection(image=image)
	return labels_response.label_annotations

def get_web_entities(image):
	# doesn't work, cannot load the image...
	client = vision.ImageAnnotatorClient()
	cloud_image = types.Image(content=image.read())
	web_entities_response = client.web_detection(image=cloud_image).web_detection	
	return web_entites_response	
