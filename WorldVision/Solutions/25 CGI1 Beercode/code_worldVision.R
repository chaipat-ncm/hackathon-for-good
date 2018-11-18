setwd("~/Documents/Hackaton")

# load packages
library(readr)
library(MultivariateRandomForest)
library(caret)

# original dataset from EM-DAT database combined with dataset from World Bank
original <- read.csv("India_Flood_2.csv", header =TRUE, sep=";")
# imputed dataset
dataset <- read.csv("India_Flood_Extended.csv", header =TRUE, sep=",")


## SUbsetting

data2<-dataset

## Omschrijven
data2$logDamage <- log(data2$Total.damage...000.US..)
data2$logMagnitude <- log(data2$Magnitude.value)

## plots
plot(data2$logMagnitude, data2$logDamage)
abline(data2$logMagnitude ~ data2$data2$logDamage)

## Modelling


set.seed(97) # Set Seed so that same sample can be reproduced in future also
# use imputed dataset as train data
train <- dataset
train$logDamage <- log(train$Total.damage...000.US..)
# use original dataset as test data
test  <- original 
test$logDamage <- log(test$Total.damage...000.US..)



#### Gradient Boosting

library(parallel)
library(doParallel)
# model1: predict damage in terms of money
cluster <- makeCluster(detectCores() - 1) # convention to leave 1 core for OS
registerDoParallel(cluster)

set.seed(280)
fitControl <- trainControl(method = 'repeatedcv',
                           number = 10,
                           repeats = 5,
                           allowParallel = TRUE)

gbmGrid <-  expand.grid(interaction.depth = 7,
                        n.trees = 100,
                        shrinkage = 0.1,
                        n.minobsinnode = 10)



fit.gb.1 <- train(Total.damage...000.US.. ~  Total.affected + Magnitude.value + Total.deaths + Year + RainFall +Temp,
                  data = train,
                  method = "gbm",
                  trControl = fitControl,
                  verbose = TRUE,
                  tuneGrid = gbmGrid,
                  na.action = na.pass
)

stopCluster(cluster)
registerDoSEQ()

pred.gb.1 <- predict(fit.gb.1,test)
summary(fit.gb.1)
#pred_dam <- exp(pred.gb.1)
compare<- as.data.frame(cbind(pred.gb.1, test$Total.affected))


compare$vdiff = compare$pred.gb.1 - compare$V2




# model 2: oredict damage in terms of people affected
cluster <- makeCluster(detectCores() - 1) # convention to leave 1 core for OS
registerDoParallel(cluster)

set.seed(280)
fitControl <- trainControl(method = 'repeatedcv',
                           number = 10,
                           repeats = 5,
                           allowParallel = TRUE)

gbmGrid <-  expand.grid(interaction.depth = 7,
                        n.trees = 100,
                        shrinkage = 0.1,
                        n.minobsinnode = 10)



fit.gb.2 <- train(Total.affected  ~  Total.damage...000.US.. + Magnitude.value + Total.deaths + Year + RainFall +Temp,
                  data = train,
                  method = "gbm",
                  trControl = fitControl,
                  verbose = TRUE,
                  tuneGrid = gbmGrid,
                  na.action = na.pass
)

stopCluster(cluster)
registerDoSEQ()

pred.gb.2 <- predict(fit.gb.2,test)
summary(fit.gb.2)
#pred_dam <- exp(pred.gb.1)
compare2<- as.data.frame(cbind(pred.gb.2, test$Total.affected))


compare2$vdiff = compare2$pred.gb.2 - compare2$V2




