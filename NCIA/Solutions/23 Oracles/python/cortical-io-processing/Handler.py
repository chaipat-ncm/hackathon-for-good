
from watchdog.events import FileSystemEventHandler

from CorticalService import CorticalService


class CorticalHandler(FileSystemEventHandler):

    pool = None
    logger = None
    write_to_directory = ""

    @staticmethod
    def on_any_event(event):
        if event.is_directory:
            return None

        elif event.event_type == 'created':
            # Take any action here when a file is first created.
            # if Handler.pool == None:
            #     Handler.pool = Pool()
            # Handler.pool.apply_async(Handler.kick_off, (event.src_path))

            CorticalHandler.logger.info("created new file at path %s." % event.src_path)

        elif event.event_type == 'modified':
            # if Handler.pool == None:
            #     Handler.pool = Pool()
            # result = Handler.pool.apply_async(Handler.kick_off, (event.src_path, "modifying"))
            CorticalHandler.kick_off(event.src_path)

    @staticmethod
    def kick_off(path):
        try:
            cortical_service = CorticalService(CorticalHandler.write_to_directory, path)
            cortical_service.process_file()
            CorticalHandler.logger.info("created semantic map for file at path %s." % (path))
        except Exception, e:
            CorticalHandler.logger.info("failed creation of semantic map for file at path %s." % (path))
            CorticalHandler.logger.exception(e)