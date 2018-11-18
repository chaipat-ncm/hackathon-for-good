from requests import get
from requests.exceptions import RequestException
from contextlib import closing
from bs4 import BeautifulSoup
import json


###


def simple_get(url):
    try:
        with closing(get(url)) as resp:
            return resp.content

    except RequestException as e:
        log_error('Error during requests to {0} : {1}'.format(url, str(e)))
        return None


def log_error(e):
    print(e)


###

path = 'events.csv'
names = {}
with open(path, 'r') as file:
    for line in file:
        sections = line.split(', ')
        code = sections[0]
        name = sections[2]
        names[code] = name

appeals = {}
for code in names:
    print(code)
    url = 'https://fts.unocha.org/appeals/' + code + '/summary'
    res = simple_get(url)

    #

    jquery = None
    for line in res.decode('utf8').split('\n'):
        if line.startswith('jQuery.extend'):
            jquery = line

    start = jquery.find('{')
    end = jquery.rfind('}')
    data = json.loads(jquery[start:end + 1])

    keys = data['appeal_clusters_json']['chart_data']['x']
    total = data['appeal_clusters_json']['chart_totals']['total_requirements']
    met = data['appeal_clusters_json']['chart_totals']['total_funding']
    unmet = data['appeal_clusters_json']['chart_totals']['unmet_requirements']

    clusters = {k: {'total': total[i], 'met': met[i], 'unmet': unmet[i]} for i, k in enumerate(keys)}

    #

    soup = BeautifulSoup(res, 'html.parser')
    divs = soup.find_all('div', 'funding-progress-bar-wrapper')

    requirements = divs[0].strong.text
    funding = divs[1].strong.text

    plan = divs[0].find('div', 'funding-progress-bar').find('div', 'plan-funding').text
    unmet = divs[0].find('div', 'funding-progress-bar').find('div', 'unmet-requirements').text
    other = divs[1].find('div', 'funding-progress-bar').find('div', 'other-funding').text

    #

    appeal = {
        "name": names[code],
        "requirements": {
            "total": requirements,
            "met": plan,
            "unmet": unmet
        },
        "funding": {
            "total": funding,
            "self": plan,
            "other": other
        },
        "clusters": clusters,
    }

    #

    url = 'https://fts.unocha.org/appeals/' + code + '/project-locations'
    res = simple_get(url)

    soup = BeautifulSoup(res, 'html.parser')
    rows = soup.find('table', 'views-table')

    locations = []
    if rows != None:
        rows = rows.find('tbody').find_all('tr')

        for row in rows:
            data = row.find_all('td')
            entry = {
                "location": data[0].text.strip(),
                "original": data[1].text.strip().replace(',', ''),
                "current": data[2].text.strip().replace(',', ''),
                "funding": data[3].text.strip().replace(',', ''),
                "pledges": data[5].text.strip().replace(',', ''),
            }
            locations.append(entry)

    #

    appeal['locations'] = locations
    appeals[code] = appeal

#

with open('appeals.json', 'w') as file:
    file.write(json.dumps(appeals))
