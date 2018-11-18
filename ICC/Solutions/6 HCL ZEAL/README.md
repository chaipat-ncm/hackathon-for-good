# Zeal

Steps for running the application:

1. Get your environment, python & project setup.
2. Install iepy using pip ( windows has better support with 3.6.5 but in linux you can run with any environment )
3. Add iepy & JAVAHOME to your path.
4. open python shell & execute iepy --download-third-party-data
5. iepy --create <Project_name>. This prompts for database name, username & password.
6. To convert from pdf to text; run src/Pdf2Text.py
7. To convert from text to csv; run src/Text2Csv.py; this will generate csv file inside src/data/text2csv 
8. To load data; python icc_master_main/bin/csv_to_iepy.py <data_file.csv>
9. On python shell python icc_master_main/bin/preprocess.py --multiple-cores=all
10. Finally execute rules - icc_master_main/bin/iepy_rules_runner.py
11. Result is stored in bin/result.json


# Common Issues with fix
1. Error- OSError: [WinError 193] %1 is not a valid Win32 application
    replace corenlp.sh with corenlp.bat
2. Error- heapsize
   updated max VM value to 1g from 3g in corenlp.bat
3. Update DB name in settings.py with your local path of icc_main.sqlite
