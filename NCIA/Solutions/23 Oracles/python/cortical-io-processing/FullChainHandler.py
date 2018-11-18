
from watchdog.events import FileSystemEventHandler

from FullService import FullService


class FullChainHandler(FileSystemEventHandler):

    pool = None
    logger = None
    urls_path = ""
    processing_path = ""
    cortical_path = ""

    @staticmethod
    def on_any_event(event):
        if event.is_directory:
            return None

        elif event.event_type == 'created':
            # Take any action here when a file is first created.
            # if Handler.pool == None:
            #     Handler.pool = Pool()
            # Handler.pool.apply_async(Handler.kick_off, (event.src_path))

            FullChainHandler.logger.info("created new file at path %s." % event.src_path)

        elif event.event_type == 'modified':
            # if Handler.pool == None:
            #     Handler.pool = Pool()
            # result = Handler.pool.apply_async(Handler.kick_off, (event.src_path, "modifying"))
            FullChainHandler.kick_off(event.src_path)

    @staticmethod
    def kick_off(path):
        try:
            full_service = FullService(path, FullChainHandler.urls_path,
                                           FullChainHandler.processing_path, FullChainHandler.cortical_path)
            full_service.process()
            FullChainHandler.logger.info("created semantic map for file at path %s." % (path))
        except Exception, e:
            FullChainHandler.logger.info("failed creation of semantic map for file at path %s." % (path))
            FullChainHandler.logger.exception(e)