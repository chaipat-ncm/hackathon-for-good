import logging
import os


class ScraperService:

    def __init__(self, write_to_directory, urls_path):
        self.urls_path = urls_path
        self.write_to_directory = write_to_directory
        self.logger = logging.getLogger(ScraperService.__name__)

    def process(self):
        current_working_dir = os.getcwd()

        os.chdir("%s/%s" % (current_working_dir, "NCIA_Scraper"))

        self.logger.debug("current dir: %s" % os.getcwd())

        # cmd = "scrapy crawl ncia -a filename=%s" % self.urls_path
        # res = os.system(cmd)
        os.chdir("%s" % (current_working_dir))
        # if res > 0:
        #     self.logger.error("something went wront in Scraping the urls of file %s" % self.urls_path)
        #     return
        # self.logger.info("scraping finished with status %s for file %s" % (res, self.urls_path))

        scraper_dir = "%s/NCIA_Scraper/ScraperOutput" % os.getcwd()
        scraper_result_filename = os.path.basename(self.urls_path).replace(".txt", "")

        initial_path = "%s/%s.json" % (scraper_dir, scraper_result_filename)
        if os.path.isfile(initial_path):
            os.rename(initial_path, "%s/%s.json" % (self.write_to_directory, scraper_result_filename))
            return

        self.logger.error("Could not find result file at path %s" % initial_path)
