import pandas as pd
import json

###

path = 'events.csv'
df = pd.read_csv(path, index_col='Event Code')

path = 'appeals.json'
with open(path, 'rb') as file:
    appeals = json.loads(file.read())

requirements = []
for code in df.index:
    appeal = appeals[str(code)]
    total = appeal['requirements']['total']
    met = appeal['requirements']['met']
    unmet = appeal['requirements']['unmet']
    other = appeal['funding']['other']

    row = [str(code), total, met, unmet, other]
    rowx = []
    for x in row:
        if 'm' in x:
            x = x[:-1] + '00.000'
        elif 'bn' in x:
            x = x[:-2] + '0.000.000'
        elif '.0' in x:
            x = x[:-2]

        if '$' in x:
            x = x[1:]

        x = x.replace(',', '.')
        x = x.replace('.', '')
        rowx.append(str(x))

    requirements.append(rowx)

path = 'requirements.csv'
with open(path, 'w') as file:
    file.write('Event Code, Total, Met, Unmet, Other\n')
    for row in requirements:
        file.write(','.join(row))
        file.write('\n')
