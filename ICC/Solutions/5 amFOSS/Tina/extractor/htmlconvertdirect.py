#!/usr/bin/env python

import html2text
from bs4 import BeautifulSoup
import requests

link = "https://www.aljazeera.com/news/2016/07/iraq-baghdad-bombings-kill-23-160703045945293.html"
response = requests.get(link).text

w = open("out.txt", "w+")
h = html2text.HTML2Text()

text = h.handle(response)
w.write(text.encode("utf-8"))
w.close()

