#
# Program to turn Geographical images into single object cutouts
#
# Run this file in the QGIS environment to cut out geometry from geographical image data.
# Before your run this, split the vector layer into seperate shapes.
#

import os

def clip_raster_by_vector(input_raster, input_vector, output_raster, overwrite=False):
    if overwrite:
        if os.path.isfile(output_raster):
            os.remove(output_raster)

    if not os.path.isfile(input_raster):
        print ("File doesn't exists", input_raster)
        return None
    else:
        params = {'INPUT': input_raster,
                  'MASK': input_vector,
                  'NODATA': 255.0,
                  'ALPHA_BAND': False,
                  'CROP_TO_CUTLINE': True,
                  'KEEP_RESOLUTION': True,
                  'OPTIONS': 'COMPRESS=LZW',
                  'DATA_TYPE': 0,  # Byte
                  'OUTPUT': output_raster,
                  }

        feedback = qgis.core.QgsProcessingFeedback()
        alg_name = 'gdal:cliprasterbymasklayer'
        #print(processing.algorithmHelp(alg_name))
        result = processing.run(alg_name, params, feedback=feedback)
        return result



input_raster = "C:/Users/mikes/Desktop/1/After/RescUAV_28917_CayHill.tif"

layer = iface.activeLayer()
for f in layer.getFeatures():
    print(f["OBJECTID"])
    output_raster = "C:/Users/mikes/Desktop/randomnaam/%d.tif" % (f["OBJECTID"],)
    input_vector = "C:/Users/mikes/Desktop/CayHill/OBJECTID_%d.shp" % (f["OBJECTID"],)
    clip_raster_by_vector(input_raster, input_vector, output_raster, overwrite=True)