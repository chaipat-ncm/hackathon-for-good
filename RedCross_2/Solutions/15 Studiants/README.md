# Redcross Satellite imagery Alignment challenge

## Installation

* Pull the repo/Download the zip
* Get the data from the drive. Put both folders into the root src dir
* Install all packages from `requirements.txt`

## How to run

In the notebook both methods are noted in the two respecting functions. They can just be called with the index of the building taken from the `*.geojson`.
The footprints from the `*.geojson` have to be converted to pictures. We have done it for only a subsample so far. With `save_footprints(…)` you can extract the building footprints as picuters and save them to the folder.

## Method 1

* _Input_: The building footprint and a surrounding cut from the bing maps
* Apply a superpixel method on the rgb-satellite imagery (in this case SLIC).
* _For each segment_: Generate mean color and fill the segment with that
* Again apply a superpixel segmentation on top of the mean pixel image
* _For each second order segment_: Lay the footprint on top of the segment and get the wrong pixels. On this binary map we use a distance map to let wrong pxiels way inside or way outside the footprint penalize more. Sum up the errors
* Each segment also gets a penality for being displaced from the displaced building input (more displacement being more rare)
* Pick the segment with lowest error
* _Output_: Center of mass of the winning segment

## Method 2

* _Input_: The building footprint and a surrounding cut from the bing maps
* Apply a superpixel method on the rgb-satellite imagery (in this case SLIC).
* Get the boundaries of the segments and apply distance transform on that binary image
* Calculate the boundaries for the footprint
* Slide the boundary over the segmented distance map
* _At each step_: Sum up the distance values (== distance to the next segment edge) for the bonudary pixels of the footprint
* If at the perfect place the edges from the footprint will align with the segmentation edges and thus be all zero
* _Output_: position from the sliding window with the smallest distance
