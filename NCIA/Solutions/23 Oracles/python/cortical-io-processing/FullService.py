import logging

from CorticalService import CorticalService
from PreprocessorService import PreprocessorService
from ScraperService import ScraperService
import os


class FullService:

    def __init__(self, vision_api_path, urls_path, processing_path, cortical_path):
        self.vision_api_path = vision_api_path
        self.urls_path = urls_path
        self.processing_path = processing_path
        self.cortical_path = cortical_path
        self.logger = logging.getLogger(FullService.__name__)

    def process(self):
        filename = os.path.basename(self.vision_api_path).replace(".json", "")

        preprocess_service = PreprocessorService(self.urls_path, self.vision_api_path)
        preprocess_service.process()

        url_file_path = "%s/%s.txt" % (self.urls_path, filename)
        if not os.path.exists(url_file_path):
            self.logger.error("File with urls could not be found at path: %s" %url_file_path)
            return
        self.logger.info("File with urls created at path: %s" %url_file_path)

        scraper_service = ScraperService(self.processing_path, url_file_path)
        scraper_service.process()
        scraper_output_file_path = "%s/%s.json" % (self.processing_path, filename)
        if not os.path.exists(scraper_output_file_path):
            self.logger.error("File with scraper results could not be found at path: %s" % scraper_output_file_path)
            return
        self.logger.info("File scraper results created at path: %s" % scraper_output_file_path)

        cortical_service = CorticalService(self.cortical_path, scraper_output_file_path)
        cortical_service.process_file()

        cortical_output_path = "%s/%s.json" % (self.cortical_path, filename)
        if not os.path.exists(cortical_output_path):
            self.logger.error("File with cortical results could not be found at path: %s" % cortical_output_path)
        else:
            self.logger.info("File cortical results created at path: %s" % cortical_output_path)
