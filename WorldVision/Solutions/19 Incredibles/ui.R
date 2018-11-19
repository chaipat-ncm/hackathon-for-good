library(shiny)
library(leaflet)

ui <- fluidPage(
  leafletOutput("mymap",height = 1000),
  br(),
  plotOutput("plotPeoplAffected")
)
