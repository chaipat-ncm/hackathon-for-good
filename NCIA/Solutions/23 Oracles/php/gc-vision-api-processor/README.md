# Hackathon NCIA Oracles

This program is used to process all images through the Google Cloud Vision API (https://cloud.google.com/vision/), and save the json body in the database.
This prevents needing to make multiple unnecessary calls to the Vision API for the same image. 

Note: Run all the commands from the project root directory.

### Prerequisites
* postgresql
* Google Cloud Vision Account ApiKey (see the `.env` file)

### Setup

1.  Setup the ```.env``` file using the ```.env.dist``` file as your guide
2.  Install dependencies: ```composer install```
3.  Create the database: ```CREATEDB your_database_name```
4.  Clear the caches: ```php bin/console doctrine:cache:clear-metadata -v && php bin/console c:c --env=prod && php bin/console c:c --env=dev```
5.  Update the database schema: ```php bin/console doctrine:schema:update --complete --dump-sql --force```
6. Create the following folders in the project directory:
   - `/resources/images/bad`
   - `/resources/images/benchmark`
   - `/resources/images/good`
   - `/resources/output/bad`
   - `/resources/output/benchmark`
   - `/resources/output/good`

### Run

1. Place images in the respective folders
   - `/resources/images/bad` for _bad_ propaganda images
   - `/resources/images/benchmark` for images to be tested
   - `/resources/images/good` for neutral or good propaganda images   
2. From the project root run the Symfony command ```php bin/console app:cloud-vision -v```
3. If an exception is thrown that the image is too large, resize the image to be below 4MB. And run the command again.
4. The output is found in the respective output folders.