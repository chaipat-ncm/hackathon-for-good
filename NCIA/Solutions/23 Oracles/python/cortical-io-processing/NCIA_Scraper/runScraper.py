import os

#############################
# Send URLs through Scraper #
#############################
# cwd = os.getcwd()
# print(cwd)

# PropagandaType = 'good/'
PropagandaType = 'bad/'

URLS_path = 'URLS/'+PropagandaType
ScraperOutput_path = 'ScraperOutput/'+PropagandaType

print os.getcwd()

img_urls_list = [pos_json for pos_json in os.listdir(URLS_path) if pos_json.endswith('.txt')]
print(len(img_urls_list))

for img in img_urls_list[:5]:
    print(img)
    print(os.system("scrapy crawl ncia -a filename=URLS/"+PropagandaType+img))
    # os.system("scrapy crawl ncia -a filename=URLS/"+PropagandaType+img+" -o ScraperOutput_path"+"_"+img+".json -t json")
    # os.system("scrapy crawl ncia -a filename=URLS/"+PropagandaType+img+" -t json -o - > "+ScraperOutput_path+"_"+img+".json")
    # os.system("scrapy crawl ncia -t json -o - > "+ScraperOutput_path+"_"+img+".json -a filename=URLS/"+PropagandaType+img+" -t --nolog -o - > "+ScraperOutput_path+"_"+img+".json")

# os.system("scrapy crawl ncia -a filename='URLS/img_11_start_urls.txt'")