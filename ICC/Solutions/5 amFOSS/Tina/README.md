# Miner

Miner is an NER(Named Entity Recognition) Miner. 

# TEXT MINING: RELATIONSHIP EXTRACTION FOR THE FACTUAL ANALYSIS DATABASE


## Problem
As part of the evidence gathering and analysis process, the Office of the Prosecutor (OTP) of the International Criminal Court has to analyse a number of written documents. These documents span a variety of formats – txt, pdf, htm(l), doc(x), xls(x) etc. – and come from a variety of sources: NGO reports, news articles, witness statements etc.
All these documents are analysed as they contain information regarding specific events that have taken place. Valuable information from these accounts is extracted and captured as entities (nodes) and relationships (edges) in a graph database - internally referred to as the Factual Analysis Database (FAD). Types of entities include persons, locations, organisations, events, dates etc. Relationships or links describe how entities interact with each other. The collected data is then analysed using domain specific software - such as IBM’s i2 and Analyst Notebook.
Currently entities and relationships are for the most part manually extracted from the documents. This is a time consuming process and the OTP would greatly benefit from automation in the entities and relationships extraction process as more efforts could be focused on other phases of the analysis.
The extraction process can be broken down into two sub-processes: Entities Extraction (which essentially is Named Entities Recognition) and Relationship Extraction. Currently the market offers a number of tools – both proprietary and open source – which allow for conducting Named Entities Recognition with a relatively high accuracy. However, the Relationship Extraction part of the process is more challenging and there seems to be an absence of relevant solutions. Therefore, the OTP would like the hackathon participants to focus on the Relationship Extraction part specifically.
## Outcome
 The envisaged outcome of the challenge is a proof of concept / working prototype of an application mining entities and relationships from txt / html / doc(x) documents and outputting them into a graphical database format – such as a .json file or a .csv file featuring a list of edges and nodes of the graph.
The OTP fully recognizes that Relationship Extraction is a non-trivial problem. Therefore, the prototype is not expected to have a high degree of accuracy. What the OTP is mostly interested in are proposed approaches, workflows, methods and algorithms for the problem at hand.
## Looking for 
Team members should have knowledge of Text Mining, Natural Language Processing and Artificial Intelligence / Machine Learning.
## Datasets 
The datasets will include:
* NGO reports
* media articles
* samples of witness statements

The OTP will also provide corresponding sample results of the Entities and Relationship extraction process from the above mentioned sources.

# Installation 

## For development

* Install Conda. Use the environment file to load all dependencies. 
 `conda env create -f environment.yaml`

### Setting up Angular environment for development

1) `npm install --save @nebular/theme @angular/cdk @angular/animations` 
2) `npm start`
