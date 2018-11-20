library(DT)
library(data.table)
library(leaflet)
# library(maps)
library(htmlwidgets) # To save the map as a web page.
library(rworldmap)
library(dplyr)
library(RJSONIO)
library(htmltools)
library(ggplot2)
filter = dplyr::filter

library(geonames)
options(geonamesUsername="kamalds")

sampledf = data.frame("country" = c('South Africa','Papua New Guinea','Indonesia','Peru'), "ElNinoEvent" = c("yes","yes","yes","yes"))
# countries = sampledf %>% filter(ElNinoEvent=="yes") %>% select(country)
# countries = countries$country
# sampledfFilt = sampledf %>% filter(ElNinoEvent=="yes") %>% mutate(latiVal = getCountryLatLong(country))

# try1
# i <- 0
# sampledf$latVal <- c() #rep(0.0,nrow(sampledf))
# sampledf$longiVal <- c() #rep(0.0,nrow(sampledf))
# sampledf
# for( i in seq(1:nrow(sampledf))){
#   sampledf$latVal[i] = getCountryLatLong(sampledf[i,"country"])$lati
#   print(sampledf$latVal[i])
#   sampledf$longiVal[i] = getCountryLatLong(sampledf[i,"country"])$longi
# }

# try2
i=1
latis = c()
longis = c()
for(coun in sampledf$country){
  # latis[i] =
  # longis[i] = getCountryLatLong(coun)$longi
  latis = append(latis,getCountryLatLong(coun)$lati)
  longis = append(longis,getCountryLatLong(coun)$longi)
  i = i + 1
}
sampledf = cbind(sampledf,latis)
sampledf = cbind(sampledf,longis)
sampledf

# iso_coun = read.csv('iso3_coun.csv',sep=" ")
mapData = read.csv('mapdata.csv')

disasters = c('Flood','Drought','Storm')
joined.data.all = sampledf %>% inner_join(mapData, by = c("country"="countryName")) %>%
  filter(disaster.type %in% disasters)

plotPeoplAffected = mapData %>% filter(alertOrNot == "HIGH" | alertOrNot == "MED" ) %>%
  mutate(elninoDisaster = ifelse(disaster.type %in% disasters,"elnino","other")) %>%
  group_by(year,elninoDisaster) %>% summarise(
    sumAffectedYearType = as.integer(mean(as.integer(total_Affected))/1000000)
  ) %>% arrange(year) %>%  ungroup() %>%
  ggplot(.,aes(x=year,y=factor(sumAffectedYearType),fill = elninoDisaster,group=interaction(elninoDisaster),color=elninoDisaster))+
  geom_bar(stat="identity")+ylab("People Affected (in Million)")



getCountryLatLong = function(countrySent){

  CountryName <- gsub(' ','%20',countrySent) #remove space for URLs

  url <- paste(
    "http://nominatim.openstreetmap.org/search?country="
    , CountryName
    , "&limit=9&format=json"
    , sep="")

  x <- fromJSON(url)
  d = data.frame("lati"=as.double(x[[1]]$lat),"longi"=as.double(x[[1]]$lon))
  return(d);
}

# allLatLongs = lapply(countries, getCountryLatLong)

# get_coordinate_country = function(countrySent) {
#   # ct = iso_coun %>% dplyr::filter(country == countrySent) %>% select(code)
#   ct = iso_coun[iso_coun[, "country"]==countrySent, ]
#   # cd = forcats::fct_drop(ct$code)c
#   cd = as.character(ct$code)
#   return(cd);
# }
#
# get.map = function() {
#   # countries = sampledf %>% filter(ElNinoEvent==1) %>% select(country)
#   countries = as.vector(sampledf[sampledf[,"ElNinoEvent"]==1,"country"])
#
#   countryIso3Codes = as.vector(unlist(lapply(countries, get_coordinate_country)))
#   # These are the ISO3 names of the countries you'd like to plot in red
#   # theCountries <- c("DEU", "COD", "BFA")
#   countryIso3Codes
#   mDf <- data.frame("country" = countryIso3Codes, event = as.vector(rep(1,length(countryIso3Codes))))
#
#   malMap <- joinCountryData2Map(mDf, joinCode = "ISO3", nameJoinColumn = "country")
#   # This will join your malDF data.frame to the country map data
#
#   # plot.map = mapCountryData(malMap, nameColumnToPlot="event", catMethod = "categorical", missingCountryCol = gray(.8))
#   mapCountryData(malMap, nameColumnToPlot="event", catMethod = "categorical", missingCountryCol = gray(.8))
# # And this will plot it, with the trick that the color palette's first
# # color is red
#
#   return(malMap);
# }


get.map.leaflet = function() {
  m <- leaflet(data = joined.data.all) %>%
    setView(lng = 50, lat = -10, zoom = 01) %>%
     addTiles() %>%  # Add default OpenStreetMap map tiles
     # addMarkers(lng=174.768, lat=-36.852, popup="The birthplace of R")
    addMarkers(lng = ~longis,
               lat = ~latis,
               popup = paste("Approx. Funding:",joined.data.all$sumFundingAll, "<br>",
                             "Possibly affect population size:",joined.data.all$total_Affected, "<br>",
                             "Alert Class:",joined.data.all$alertOrNot, "<br>",
                             "Disaster Type:",joined.data.all$disaster.type, "<br>",
                             "Country:",joined.data.all$country, "<br>"
                             ),
               label = ~htmlEscape(~sumFundingAll)
              )

  return(m);
}


server <- function(input,output, session){

 data <- reactive({
   x <- df
 })

 # output$plot1 <- renderImage({
 #    # A temp file to save the output. It will be deleted after renderImage
 #    # sends it, because deleteFile=TRUE.
 #    outfile <- tempfile(fileext='.png')
 #  })

  output$plotPeoplAffected = renderPlot({
      plotPeoplAffected
    }, height = 400, width = 600)

 output$image1 = renderPlot({
     # ggplot(spaVisitors,aes(x=breakout,y=value)) + geom_bar(aes(fill = variable),stat = "identity",position = "dodge")

   }, height = 400, width = 600)

 output$mymap <- renderLeaflet({
   df <- data()

   # m <- leaflet() %>%
   #    addTiles() %>%  # Add default OpenStreetMap map tiles
   #    addMarkers(lng=174.768, lat=-36.852, popup="The birthplace of R")
   m = get.map.leaflet()
   m
 })
}
