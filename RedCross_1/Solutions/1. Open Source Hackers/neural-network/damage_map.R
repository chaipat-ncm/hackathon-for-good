###############actual code##################
library(rgdal)
if (!require(geojsonio)) {
  install.packages("geojsonio")
  library(geojsonio)
}
library(sp)
library(maps)
library(ggmap)
library(maptools)

#upload data
data_file <- "/Users/jdabrowska/Downloads/12436053980344363593.geojson"
data_json <- geojson_read(data_file, what = "sp")
plot(data_json)

data_file_raw <- "/Users/jdabrowska/Downloads/609587787987558586.geojson"
data_json_raw <- geojson_read(data_file_raw, what = "sp")
plot(data_json_raw)

install.packages("ggplot2")
library(ggplot2)

#show the summary of data
summary(data_json_raw)
summary(data_json)

#frame json as data frame
data_df <- as.data.frame(data_json)
data_df_raw <- as.data.frame(data_json_raw)

install.packages("dplyr")
library(dplyr)

#join data
data_join <- left_join(data_df_raw, data_df, by= "OBJECTID", "osm_way_id")

print(data_join)

summary(data_join)

#extract coordinates
coordinates_buildings <- coordinates(data_json_raw)
coordinates_test <- coordinates(data_json)

coordinates_build_df <- as.data.frame(coordinates_buildings)
coordinates_test_df <- as.data.frame(coordinates_test)

coor_long <-coordinates_buildings$V1
coor_lat <- coordinates_buildings$V2
library(sp)

#playing with data
plot(data_json, lwd=0.1)
summary(data_json)
data_json@polygons[[1]]
data_json@polygons[[2]]@area
data_json@polygons[[1]]@Polygons[[1]]@coords

plot(data_json@polygons[[1]]@Polygons[[1]]@coords)
plot(data_json@polygons[[1]]@Polygons[[2]]@coords)
plot(data_json@polygons[[2]]@Polygons[[1]]@coords)
plot(data_json@polygons[[2]]@Polygons[[2]]@coords)
plot(data_json@polygons[[3]]@Polygons[[1]]@coords)

#making data available for plotting
data_json_fr <-fortify(data_json)

print(geom_polygon(aes(long, lat, group = group), data = data_json_fr, 
                              colour = "green"))
print(geom_polygon(aes(long, lat), data = data_json_fr, colour = "green"))

#upload joined data in CSV, with longitudes and latitudes involved
data_csv <- read.csv("/Users/jdabrowska/Downloads/TrainingDatasetLongLat.csv", header = TRUE, sep=",")
print(data_csv)
plot(data_csv$X, data_csv$Y)

#plotting the map
long <- data_csv$X
lat <- data_csv$Y
damage_level <-data_csv$X_damage

heat_map <- ggplot(data_csv, aes(x =long, y= lat, colour = damage_level)) + geom_point()  
              
heat_map + theme(legend.title=element_blank())

heat_map + scale_colour_discrete(values=c("red", "orangered", "orange", "yellow", "white"), 
                  name="Damage level",
                  breaks=c("destoryed", "significant", "partial", "unknown", "none"),
                  labels=c("destoryed", "significant", "partial", "unknown", "none"))

#printing the result
print(heat_map)

#trying to implement the map as underneath layer - failed due to api problem 
#install.packages("osmar")
#library(osmar)
#get_osm_data("www.openstreetmap.org/#map=13/18.0690/-63.0715&layers=H")

#getbb(("St Maarten", display_name_contains = "Sint Maarten", viewbox = 18.0947, -63.0479,
       #format_out = " sf_polygon",
       #base_url = "https://planet.openstreetmap.org/pbf/planet-latest.osm.pbf",
       #featuretype = "settlement", limit = 10, key = NULL,
       #silent = TRUE, poly_num = c(1, 1))) 



#############workings###############


#print(coordinates_test_df)

#data_join_test <- left_join(data_df, coordinates_test_df, by = NULL)

#install.packages("ggplot2")
#library(ggplot2)
#ggplot_build(coordinates_test_df)
    #ggplot(data_json, aes(x = coordinates_test_df$V1, y = coordinates_test_df$V2))

#plot(coordinates_test, coordinates_buildings)

#print(coordinates_test attr)


#print(data_join)

#plot(data_join)
#plot(data_df$OBJECTID, data_df_raw$OBJECTID)


#mapImage <- get_osm()


#plot(data_json$OBJECTID, data_json$X_damage)
#plot(data_json_raw$OBJECTID, data_json_raw$building)
