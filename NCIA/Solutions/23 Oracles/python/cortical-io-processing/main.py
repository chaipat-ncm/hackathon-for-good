import logging
from multiprocessing import Pool

from FullChainHandler import FullChainHandler
from Handler import CorticalHandler
from PreprocessorHandler import PreprocessorHandler
from ScraperHandler import ScraperHandler
from Watcher import Watcher

FORMAT = "%(asctime)-15s %(message)s"

# def set_logger():
#     CorticalHandler.logger = multiprocessing.get_logger()
#     CorticalHandler.logger.setLevel(logging.DEBUG)
#     ch = logging.StreamHandler()
#     ch.setLevel(logging.DEBUG)
#     ch.setFormatter(FORMAT)
#     CorticalHandler.logger.addHandler(ch)


def start_cortical_watcher():
    listening_to_path = "%s/../../processed" % os.getcwd()
    write_to_dir = "%s/../../semantic-maps" % os.getcwd()
    logging.getLogger("main").info("watching directory: %s" % listening_to_path)
    CorticalHandler.logger = logging.getLogger("Handler")
    CorticalHandler.write_to_directory = write_to_dir
    w = Watcher(listening_to_path, CorticalHandler())
    w.run()


def start_preprocessor_watcher():
    listening_to_path = "%s/NCIA_Scraper/VisionAPI_Output" % os.getcwd()
    PreprocessorHandler.write_to_directory = "%s/NCIA_Scraper/URLS" % os.getcwd()
    PreprocessorHandler.logger = logging.getLogger("Handler")
    PreprocessorHandler.vision_api_output_path = listening_to_path
    w = Watcher(listening_to_path, PreprocessorHandler())
    w.run()


def start_scraper_watcher():
    listening_to_path = "%s/NCIA_Scraper/URLS" % os.getcwd()
    ScraperHandler.write_to_directory =  "%s/../../processed" % os.getcwd()
    ScraperHandler.logger = logging.getLogger("Handler")
    ScraperHandler.vision_api_output_path = listening_to_path
    w = Watcher(listening_to_path, ScraperHandler())
    w.run()


def start_full_service():
    listening_to_path = create_full_service()
    w = Watcher(listening_to_path, FullChainHandler())
    w.run()


def create_full_service(verdict):
    listening_to_path = "%s/NCIA_Scraper/VisionAPI_Output/%s" % (os.getcwd(), verdict)
    FullChainHandler.urls_path = "%s/NCIA_Scraper/URLS" % os.getcwd()
    FullChainHandler.processing_path = "%s/../../processed/%s" % (os.getcwd(), verdict)
    FullChainHandler.cortical_path = "%s/../../semantic-maps/%s" % (os.getcwd(), verdict)
    FullChainHandler.logger = logging.getLogger("FullChainHandler")
    return listening_to_path


if __name__ == '__main__':

    logging.basicConfig(level=logging.DEBUG, format=FORMAT)

    import os

    os.chdir(os.path.dirname(__file__))
    verdict = "bad"
    create_full_service(verdict)
    full_chain_handler = FullChainHandler()

    read_dir = "%s/NCIA_Scraper/VisionAPI_Output/%s" % (os.getcwd(), verdict)
    files = [pos_json for pos_json in os.listdir(read_dir) if pos_json.endswith('.json')]

    for file in files:
        full_chain_handler.kick_off("%s/%s" % (read_dir, file))


    # start_full_service()
    # start_cortical_watcher()
    # start_scraper_watcher()
    # start_preprocessor_watcher()