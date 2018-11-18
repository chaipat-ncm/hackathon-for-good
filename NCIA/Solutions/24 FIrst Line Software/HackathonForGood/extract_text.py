#!/usr/bin/env python3

try:
    from PIL import Image, ImageEnhance
except ImportError:
    import Image, ImageEnhance
import pytesseract
import re
from nltk.corpus import brown
import nltk
import os

nltk.download('brown')
word_list = brown.words()
word_set = set(word_list)


class ImageContent:
    def __init__(self, image_name, content):
        self.image_name = image_name
        self.content = content


def to_file_name(directory, file_name):
    if directory.endswith('/'):
        directory = directory[:-1]
    return os.path.join(directory, file_name)


def ocr(directory, image_name):
    try:
        img = Image.open(to_file_name(directory, image_name))
    except OSError:
        print('failed to parse image' + image_name)
        return ''

    try:
        img = img.convert('LA')
    except OSError:
        img = img

    img = img.resize([img.width * 2, img.height * 2])

    img = ImageEnhance.Brightness(img).enhance(0.1)
    img = ImageEnhance.Contrast(img).enhance(2.0)
    img = ImageEnhance.Contrast(img).enhance(4.0)
    img = ImageEnhance.Brightness(img).enhance(0.2)
    img = ImageEnhance.Contrast(img).enhance(16.0)

    try:
        vel = pytesseract.image_to_string(img)
    except UnicodeDecodeError:
        vel = -1

    return vel


def extension(file):
    parts = file.split('.')
    return parts[len(parts) - 1].lower()


def sanitize(unsanitized):
    if unsanitized == '':
        return ''
    else:
        # strip new lines
        sanitized = unsanitized.replace('\n', ' ')
        sanitized = re.sub('\s+', ' ', sanitized)

        words = sanitized.split(' ')
        probable_words = []

        for word in words:
            if len(word) == 1:
                continue
            elif word in word_set:
                probable_words.append(word)

        return ' '.join(probable_words)


def to_file_contents(directory, image_name):
    file_path = to_file_name(directory, image_name)
    allowed_extensions = ['jpg', 'png', 'jpeg']

    if extension(file_path) in allowed_extensions:
        content = ocr(directory, image_name)
        content = sanitize(content)
        return ImageContent(image_name, content)
    else:
        return ImageContent(image_name, '')
