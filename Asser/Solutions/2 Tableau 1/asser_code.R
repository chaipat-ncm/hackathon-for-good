library(xgboost)
library(Matrix)
library(readr)
library(stringr)
library(caret)
library(car)
library(rpart)
setwd()
data1 <- read.csv("Grain_data2.csv")
attach(data1)

model <- glm(Grabbed~Palm.Oil+Rice+Maize+Livestock+Fruit+Cotton+Crops+Investment
             +Hectares+Algeria+Angola+Argentina+Armenia+Algeria+Angola+Argentina+Armenia+Australia+Belize+Benin+
               Bolivia+Brazil+Bulgaria+Burma+Bulgaria.1+Burma.1+Cambodia+Cameroon+China+Ivory.Coast+Colombia+Congo
             +Czech.Republic+Democratic.Republic.of.the.Congo+East.Timor+East.Timor+Egypt+Ethiopia+Fiji
             +Gabon+Ghana+Guinea+Guinea.Bissau+Hungary+Indonesia+GDP.per.capita.PPP.Base+GDP.per.capita.Base+GDP.PPP.Base+GDP.Base, data = data1, family = binomial())

(#get rid of low significance variables from above+rerun)
#use predict() to calculate predicted probabilities for countries we have, to get vaues to plot in tableau (may need to combine all rows with same country in a general way eg mean)
predictions<-predict(model, data1,type = "response")
write.csv(predictions, "predictions.csv")  
#Decision tree using rpart() with the exp(coef(model)) or using the model
tree<-rpart(Grabbed~Palm.Oil+Rice+Maize+Livestock+Fruit+Cotton+Crops+Investment
      +Hectares, data = data1, method = "anova",control=rpart.control(minsplit=70, cp=0.001))
plot(tree)
text(tree)
