import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler


class Watcher:
    # DIRECTORY_TO_WATCH = "/path/to/my/directory"

    def __init__(self, dir_to_watch, event_handler):
        self.observer = Observer()
        self.DIRECTORY_TO_WATCH = dir_to_watch
        self.event_handler = event_handler

    def run(self):

        self.observer.schedule(self.event_handler, self.DIRECTORY_TO_WATCH, recursive=True)
        self.observer.start()
        try:
            while True:
                time.sleep(5)
        except:
            self.observer.stop()
            print "Error"

        self.observer.join()


