import subprocess

import os
import iepy
from iepy.instantiation.instance_admin import InstanceManager

def execIepy(name):
    InstanceManager(name, "en").create()

def execCsv2Iepy(path2Csv):
    cmd = "C:/ws/hfg/code/zeal/src/icc-doc/bin/csv_to_iepy.py "+path2Csv
    # os.system(cmd)
    subprocess.Popen(cmd)








if __name__ == "__main__":
    # execIepy("icc-doc")
    execCsv2Iepy("C:/ws/hfg/code/zeal/src/data/text2csv/0__PDF.txt.csv")



