# R code for retrieving tweets from twitter

#install.packages("stringr")
#install.packages("twitteR")
#install.packages("ggplot2")
#install.packages("reshape2")
#install.packages("gridExtra")
#install.packages("dplyr")
#install.packages("tm")
#install.packages("wordcloud")
#install.packages("wordcloud2")
  
library(stringr)
library(twitteR)
library(ggplot2)
library(reshape2)
library(gridExtra)
library(dplyr)
library(tm)
library(wordcloud)
library(wordcloud2)

# functions

CleanText <- function(df) {

  text <- iconv(x=df[,"text"], from= 'utf-8', to = 'ASCII', "byte")
  text <- gsub("<[^>]+>", "",text)                                    #removes alles tussen <...>
  text <- gsub("https[^ \n\t\v\f]+", "", text)                        #removes links, also when there are multiple links, and not just at the end of the line (\n)
  text <- str_trim(text)                                              #removes leading and trailing spaces
  text <- sub("[ .]+$", "", text )                                    #removes trailing punctiation and spaces (also spaces because: ..  .. can happen)
  df[,"text_clean"] <- text
  df <- df[df$isRetweet==FALSE,]                                      #removes retweets

  return(df)
}

#######################################################################

getLocation <- function(x) {
  y <- try(getUser(x))                                        #twitteR function: get User from screenname
  if(class(y)=="try-error") {
    return("")
  }
  else {
    z <- y$location                                        #get location out of 'user' class
    print(z)
    return(z)
  }
  #return location
}

#######################################################################

LocationList <- function(df) {
  unique_names <- unique(df$screenName)
  bio <- sapply(unique_names, function(x) getLocation(x))
  #first output was this bio. now the following has been added and tested (may 14)
  bio <- unlist(bio)
  screenName <- names(bio) #going wrong
  bio_and_names <- cbind(screenName, bio)
  output <- base::merge(df, bio_and_names, all.x = TRUE, all.y = FALSE)
  return(output)
}

#######################################################################

EnrichTweets <- function(df) {
  df$date = format(as.POSIXct(df$created,format="%Y-%m-%d %H:%M:%S"),"%Y-%m-%d")
  df$time = format(as.POSIXct(df$created,format="%Y-%m-%d %H:%M:%S"),"%H:%M:%S")

  df <- LocationList(df)

  return(df)
}

#######################################################################

# set up the handshake

api_key <- "pU6wCDIKFor5tQVjwpJjcY1JY"
api_secret <- "iccWLUmhkp4QCPRcjQl0qeOTT0RsRgOAkEoOcl2SZa5Xnz372d"
access_token <- "849633291426779136-r2blfiSl6bRETJ4xAipkbgOGQVhRAMF"
access_token_secret <- "58EfnmKB8jdGo5Yrx7XiwN31sDQ2MSD79RP2kpklFOLn8"

setup_twitter_oauth(api_key, api_secret, access_token, access_token_secret)

# get the arguments (keywords) from cmd (java back end)

#args = commandArgs(trailingOnly = TRUE)

#x = paste(args[1], args[2], sep = " + ")

# search the tweets based on the received keywords

tweets <- searchTwitteR(searchString = "landgrab", n=100, lang = 'en', locale = 'congo') 

# convert to data frame

tweets_DF <- twListToDF(tweets)

# clean tweets (punctuation, links, emojis, retweets)

clean_tweets <- CleanText(tweets_DF)

# enrich tweets (location, time, date)

enriched_tweets <- EnrichTweets(clean_tweets)

# remove irrelevant variables from the data frame

tweets_Fin_DF = enriched_tweets

tweets_Fin_DF$favorited = NULL
tweets_Fin_DF$favoriteCount = NULL
tweets_Fin_DF$replyToSN = NULL
tweets_Fin_DF$truncated = NULL
tweets_Fin_DF$replyToSID = NULL
tweets_Fin_DF$replyToUID = NULL
tweets_Fin_DF$retweetCount = NULL
tweets_Fin_DF$isRetweet = NULL
tweets_Fin_DF$retweeted = NULL
tweets_Fin_DF$statusSource = NULL
tweets_Fin_DF$id = NULL
tweets_Fin_DF$longitude = NULL
tweets_Fin_DF$latitude = NULL

# adding the location and topic variables based on the provided keywords (first keyword is the location, second keyword is the topic)

#tweets_Fin_DF$Location = args[1]
#tweets_Fin_DF$Topic = args[2]

# save the final data frame as a .csv file

write.csv(tweets_Fin_DF, "congo_landgrab.csv")
