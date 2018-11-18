# The Illustrious Guild of Hacksmiths Proudly Presents

# Notes towards a general model for efficient allocation of aid funds in anticipation of natural disasters

Presented at: the Hackathon for Good 2018, as part of the WorldVision challenge.

Team members: Frederick Sondheimer & Federico Pereiro.

## Abstract

We develop in broad sketches an adaptive model to improve the predictive task of allocation of resources in anticipation of natural disasters. The model is modular on nature and specifies four models based on historical and scientific data to generate optimal predictions. The model allows for the use of different qualities of data while providing the best possible results from it. The model is also intended to spur research into critical variables that are usually not well ascertained (such as the quality of an aid intervention) which when researched can vastly improve the overall results.

## Inspiration

We are inspired by both the ideals and the overall approach of the Smallpox Erradication Program (1965-1980). This global program accomplished the heroic mission of eliminating one of humanity's oldest and deadliest diseases; the program succeeded despite severe financial, logistical and political limitations (Henderson 2008) because it had an unconventional approach. This approach sharply contrasted with traditional mass vaccination efforts, and instead consisted of 1) data-driven interventions (ring vaccination surrounding outbreaks); 2) done in an extremely speedy manner; and 3) with constant evaluations of the effectiveness of the interventions (from vaccine quality to outbreak reports to vaccine delivery).

The lessons of smallpox erradication (particularly its reliability on precise information for timely and tremendously effective interventions) can be perhaps applied to improve the allocation of aid in anticipation for natural disasters. We hope this work can further this aim.

## Introduction

Natural disasters are recurring phenomena that have a great impact on humanity worldwide, particularly for people living in developing countries where support infrastructures are lacking. In these cases, foreign aid (both public and private) is critical to ensure that disaster relief is promptly and sufficiently administered.

Because natural disasters are recurring phenomena, there are scientific (geological, climatological) models for predicting their incidence and impact. By combining these models with historical data on the human and economic impact of disasters, it is possible to estimate the incidence and impact of future disasters, with a view to allocate future resources in the most impactful way whenever a new disaster strikes again.

A cursory review of the existing literature shows that predicting the amount of aid needed for natural disasters is hard. The literature also shows that there is great potential in improving the methods of estimation of required aid for natural disasters. Following GAO 2008, which studies the effectivity of prediction of aid needs by the United States' Federal Emergency Management Agency (FEMA): "Decision makers need accurate information to make informed choices and learn from past experiences (...) by three months after of a declaration of a natural disaster, estimates are usually within 10% of actual cost."

Based on GAO 2008 data, initial estimates after a disaster is declared are on average 50% different to actual costs. This is even more significant because FEMA operates on a developed and data-rich environment; these conclusions should naturally apply to developing countries to an even greater extent.

On how to estimate aid needs: "FEMA officials identified several ways in which FEMA takes past experience into account and uses historical data to inform its cost estimation processes for any given disaster. For example (...) they use demographic data such as population size and average hosehold income and the national average of program costs to predict average cost for expected applicants." (GAO 2018)

Becerra et al 2012 state that there is plenty of work to be done in this area: "How much does foreign aid really increase in the aftermath of large disasters? Are aid surges sizable in relation to the estimated economic damages caused by disasters? And what determines the actual size of the surges? As far as we could find, no one has ever looked at these questions systematically, in spite of their obvious importance."

When designing the structure of our model, we have made use of established risk-analysis practices of banking and insurance companies to evaluate the default and disaster risks. These instutions have developed useful models to minimize exposure and maximize return on investment. We believe it is possible and practical to adapt some of these analytical techniques for the purpose of optimally allocating aid for natural disasters. An example of this is the R-FONDEN initiative by the Government of Mexico, which has created a system for assessing the probability and impact of natural disasters on infrastructure with the purpose of predicting the need for aid for infrastructure reconstruction after natural disasters (Ishizawa 2011).

## The model

The model we present attempts to generate predictions from existing data and suggest where aid money will be the most impactful to alleviate impending natural disasters.

The main characteristic of the model is that it is modular. The modules fulfill predefined roles and their order is fixed. All the modules take a set of data as input and produce another set of data as output. All the modules take raw data as input; all except the first module take the outputs of previous modules as an input.

A model consisting of a set of interconnected modules in a fixed sequence allows for two things: 1) a high level intuititive and descriptive understanding of the entire process of prediction and estimation; and 2) very favorable conditions for carrying out an incremental process of perfection and experimentation of each of the individual modules.

A model with a high level intuitive description is not only useful for analysts and policy makers, who can build on the solid foundation of a coherent and logical model; it can also help donors understand the whys and hows of where aid shall be needed, hence being critical not just for optimal allocation of aid, but also for increasing its total amount.

As for the modular approach, it provides ample room for iterating over better datasets and estimation techniques; at the same time, it allows for making initial rough estimations in the absence of precise data or complex estimation techniques. For example: a module for calculating the future incidence and impact of earthquakes can start as a simple regression of previous disasters. In this incarnation, the module can provide valuable input to other parts of the model and generate results. When better data (or a more sophisticated geological model) is availble, the module can be replaced or enhanced. Also, during the process of improving the module, because its results are always in context and always in close communication to the other modules, it is easier to gauge its validity (at least in the initial stages, and particularly in the absence of precise data and/or solid statistical methods). It is also possible that more solid modules can help test the validity of modules with less data or more imprecise estimation functions.

The model is composed by four modules:

- **A disaster prediction module (DPM)**: this module uses historical data and scientific (geological, climatological) models to predict the future incidence and magnitude of disasters. The output of this module is a set of probability functions for different regions and disasters. The magnitude of the disasters is measured in a variable of a physical nature, such as the Richter scale, flood height and area, wind speed, etc. The intuitive understanding of DPM's output is to gauge the extent of a disaster without taking into account any human or economic factors; the same storm in two geographically identical islands (where one island is uninhabited and the other one is heavily populated) should produce the same results in DPM.

- **A disaster incidence module (DIM)**: DIM takes the output of DPM plus data on the past impact of previous disasters on human lives and infrastructure and produces an estimation of how much damage will be created by impending disasters. DIM essentially combines DPM's physical distributions and converts them into statistical distributions of the utmost importance for people affected by natural disasters. By combining probabilistic data of a physical nature with other data such as geographical information of population distribution and the characterstics of existing infrastructure, DIM estimates the true impact of a catastrophe.

- **An aid requirement module (ARM)**: two disasters that could be equal in terms of human suffering can still have vastly different requirements for aid; for example, if the disasters happen on two countries with very different levels of emergency preparedness, development or access to own emergency funds. ARM takes DIM data as input, plus historical data on the cost of previous disasters and corresponding aid efforts. ARM assigns an estimated cost (or an probability function of the cost) for the disasters provided by DIM.

- **An aid priority module (APM)**: in an ideal world where there's sufficient access to aid funds, ARM would be enough since it specifies an estimated list of disasters with their incidences and costs. In the real world, unfortunately, aid funds are scarce and two equal disasters might receive unequal attention. APM intends to maximize the impact of the next thousand, million or billion dollars that are going to be spent in aid following the model's prescriptions. APM takes ARM data as input and adds a bias parameter, which can be based not just on current but also historical data, of which disasters (of a certain type and in a certain area) generally tend to be more neglected for political or historical reasons. In short, APM aims to predict bias in aid allocation and allow donors to maximize their impact by helping the disasters that are the most likely to be neglected. This provides both a way to generate more funds (because the impact is bigger and the case is more compelling) and a placeholder for a systematic study of aid bias, which can help treat the problem in more direct ways. An advanced APM module can also provide something even more valuable than a ponderated and debiased list of impending disasters: it can tell donors to spend the first X millions on a certain event; then the following Y millions on a different event. In other words, it can provide for an incremental list of aid interventions, measured by debiased impact.

## Next steps

We believe the true value of this model comes from its ability to spur challenging questions to improve the quality and quantity of highly relevant data. The modular approach also allows for vast independent yet interconnected improvement of the models used.

We see two overarching next steps with great potential to put this model into practice.

### StrongARM: a data initiative (6 months)

The ARM module has the greatest gap between existing and potentially available data. We propose a dedicated workstream to coordinate a central database of post-disaster aid spending. Priorities would be: categorizing existing data into a standard format, leveraging historical data from individual organizations and then proposing ongoing ways of collecting and reporting standard data to feed the model going forward.

With consistent and abundant data, the ARM module can then become a credible tool for performing fact-based estimations of aid needs.

### End-to-end pilot (24 months)

To smooth the eventual rollout of the entire model, it is necessary to understand how it can be embeded in the actual practice of the deployment of aid, from the donor to the field through all the steps in between. To achieve this, we propose identifiying a small scale regional pilot project (ideally geared to a single type of recurring catastrophe). Criteria to identify the most appropriate pilot would include: aid need and potential for impact, existing data availability, availability of stakeholders along the entire chain.

### Module improvement (ongoing with initial 6 months push)

Below is a list of directions for future improvements, based both on research/data (DATA) questions and on prediction models based on data (MODEL).

#### Prediction (DPM)

- MODEL: use of probabilistic distribution instead of point-based estimates.
- DATA: incorporation of real time climatological and sysmic data to update forecasts: early warning system!
- DATA: curation, publication and updates of open databases on climatologic and sysmic events.
- DATA+MODEL: wherever available, directly query scientific models that are maintained and updated by scientific organizations.

#### Impact (DIM)

- MODEL: scenario analysis for extreme events to take into account cascade effects that are not taken into account in data-driven models.
- DATA+MODEL: use of geographical information systems to assess population and infrastructure distributions and effects. Existing work (ranging from government initiatives such as RFONDEN, to hackathon projects such as the one done by our colleagues!) can be lveraged.

#### Requirement (ARM)

- DATA: creation of an incremental yet systematic database of the cost of aid interventions (aggregated and categorized) which will be of a fundamental nature for estimating the cost of future interventions.
- MODEL: as consistent and sufficient data becomes available, make a paradigm shift from top-down estimation (based on historic spending) to bottom-up estimation based on granular and comparative data.
- DATA: further study on the impact of catastrophes (i.e.: before and after) on core health and development statistics (mortality, morbility, poverty rate) to evaluate the sufficiency of aid interventions. This information not only allows for absolute measures of aid effectiveness, but it also can serve as an ongoing gauge on the quality of present and future aid interventions.
- MODEL: use of a value-at-risk style metric to estimate the total needed funds within a given confidence level.

#### Priority (APM)

- MODEL: based on a combination of ARM data and studies on the quality of aid interventions, create a systematic model of aid bias which can help highlight the most neglected disasters (by disaster type, region or seasonality).

## On Implementation

The implementation of the model can be done on any number of programming languages and architectures. We however choose a web application paradigm, which offers incomparable flexibility and accessibility. We believe that the possibility of accessing data and estimation models through a web browser is too compelling to ignore, particularly when the inputs to the model (and some critical parameters) can be modified on the fly by different users who can then immediately see the impact of those changes. Interactive tools can inform and persuade donors (including the general public) of the need for providing aid and how much of a difference can be made by taking action.

A web application approach can then be structured in the following way: a web server (WS) listens to requests for data and model calculation. Model calculation is a batch process triggered and orchestated by WS that can cache results for different inputs. In any case, recalculation of the model (or of the modules whose parameters are usually modified) should be fast (< 2 seconds) to allow for interactivity.

WS can then execute the modules in succession, providing data for them and collecting the data produced by them. Caching and data management are WS' responsibility. The modules are merely concerned with receiving input (which should conform to a strict and well defined structure) and producing output (of a similarly strict and well defined structure).

If the programming language(s) or tool(s) used to implement the modules change, then the parts of WS that interact with them should also be changed; however, those changes should not be hard to implement or test, since they can usually consist of executing a given process (the program implementing the module) and passing some environment parameters to it. It is also worth noticing that if a given module is rewritten in a new tool or programming language, none of the other modules should be changed or adapted. Their coordination hinges instead on the sequence of execution (that is, the model) and the modules strict enforcement of validations over inputs and outputs.

Because we suggest building a web application, the client necessarily has to be a javascript application. It is possible to completely generate each page on the server, but we instead suggest a more flexible and efficient paradigm where the browser loads a javascript application which after its initial load merely requests or sends data to WS. This allows for faster page redraws and a much improved user experience. Communication between the javascript client and WS is performed through XMLHTTP requests carrying JSON (which is commonly referred to as AJAX).

An interesting question arises on who should perform validations on input data and on the output of a module. While it could be possible for WS to implement validation, we instead suggest that each module be self contained and perform strict data validations on its input. A set of tests (internal to each module) can be run to reduce the possibility of bugs and regressions.

## Our implementation

Our current implementation of the model is of an extremely unsophisticated nature, owing to the fact that the entire work has been done in ~30 continuous hours. It, however, contains the broad strokes of the four modules we present in our model description.

The modules currently use mock data. We didn't have enough time to make them retrieve information from the CSV datasets. The modules also use a very simplistic logic for estimating aid need; a more sophisticated analysis and set of modules is on an Excel spreadsheet contained in the `docs` directory.

Our choice of technologies is node.js for WS; separate node.js programs for the individual modules; and a javascript frontend library for the client. Other implementations could, for example, use Python or R programs to implement the individual modules, particularly if those modules use sophisticated probabilistic functions. If some (or all) modules were to be written in a different programming language, less than 10 lines of WS code should be changed.

To run the implementation, please follow these steps:

- [Install node.js](https://nodejs.org/en/download/) if you do not have it installed.
- Install git if you do not have it installed.
- Clone the repository by entering this command at your terminal: `git clone https://github.com/fpereiro/hackathon-for-good`.
- Enter the folder and run `npm install --no-save` to install the dependencies.
- Enter the `server` folder.
- Enter the command `node server`.
- To access the client, open your browser and enter this address in the URL bar: `localhost:1444`.

To provision a bare Ubuntu server on which to run the server so it can be publicly available:

- Requires you to be on a Linux/BSD machine.
- Modify `$HOST` to point to the user and IP address of your server.
- Run `./provision.sh` once.
- Run `./deploy.sh` the first time, and any time that you want to deploy your changes.

## Datasets

Part of the contribution of this work is to provide a list of datasets in CSV format which can be directly accessed and used by future researchers. These are available under the folder `datasets`. Here is a list of the datasets we chose to analyze:

- Becerra et al (2012) Disaster Data: a list of natural disasters, classified by country, year, impact (absolute & relative deaths, absolute & relative economic losses), aid surge (estimated aid directed to the catastrophe). The list is curated and eliminates disasters with aid surges that could be attributed to other issues. The list takes two sources into account: UN-FTS and OECD-ODA. The latter source is more useful because it allows to track a surge in donations after a particular disaster, and thus differentiate it from the normal expectd volume of aid to the target country that would have happened anyway.

- EMDAT (2018) Natural Disasters: a compendium of natural disasters from 1900 to date. Includes estimations of deaths and economic damages.

- ReliefWeb (2018): a list of humanitarian crises of all kinds coupled with a number of affected persons.

- UNFTS Appeals Report (2008-2018): a list of aid requests combined with the amount of aid pledged and sent.

## References

- Becerra, O., Cavallo E., Noy, I. (2012). Foreign Aid in the Aftermath of Large Natural Disasters. Inter-American Development Bank. Available at https://publications.iadb.org/bitstream/handle/11319/4056/Foreign%20Aid%20in%20the%20Aftermath%20of%20Large%20Natural%20Disasters.pdf

- EM-DAT (2018). Available at https://www.emdat.be

- GAO (2008). FEMA Can Improve Its Learning from Past Experience and Management of Disaster-Related Resources. Report to Congressional Committees. Available at https://www.gao.gov/assets/280/272488.pdf

- GAO (2000). FEMA's Estimates of Funding Requirements Can Be Improved. Report to the Subcommittee on VA, HUD,
and Independent Agencies, Committee on Appropriations, U.S. Senate. Available at https://www.gao.gov/assets/230/229493.pdf

- InfoRM (2018). InfoRM 2019 Report. Available at http://www.inform-index.org/LinkClick.aspx?fileticket=Z80oBfJxnl8%3d&tabid=107&portalid=0http://www.inform-index.org/Results/Global

- Ishizawa (2011). Mexico Disaster Risk Financing Strategy and Evolution of FONDEN's Damage and Loss Assessment System. Available at http://siteresources.worldbank.org/EXTDISASTER/Resources/8308420-1361321795200/OI-Presentacion-BBL-MX-DRFI.pdf

- Henderson (2009). Smallpox: The Death of a Disease - The Inside Story of Eradicating a Worldwide Killer. Prometheus Books; 1st edition.

- ReliefWeb (2018). Historical Figures. Available at https://data.humdata.org/dataset/reliefweb-crisis-figures

- United Nations Financial Tracking Service (UNFTS) (2018). Appeals report. Available at https://fts.unocha.org/appeals/overview/2018

- WorldVision (2018). Preparing for emergency: forecasting humanitarian finance. Available at https://github.com/bbahov/hackathon-for-good/blob/master/WorldVision/Instructions.ipynb
