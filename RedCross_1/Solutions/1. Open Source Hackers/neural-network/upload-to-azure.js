const rp = require('request-promise');
const fs = require("fs");

// envars
const TRAINING_KEY = process.env.TRAINING_KEY;
const PREDICTION_KEY = process.env.PREDICTION_KEY;
const PROJECT_ID = process.env.PROJECT_ID;

// load tags
const tags = JSON.parse(fs.readFileSync('build/tags.json'));

// 1. get list of files @TODO: get from kagal
const files = [
    '451_RescUAV_12917_Philipsburg.tif_partial.tif',
    '572_RescUAV_12917_Philipsburg.tif_partial.tif',
    '577_RescUAV_12917_Philipsburg.tif_partial.tif',
    '583_RescUAV_12917_Philipsburg.tif_partial.tif',
    '590_RescUAV_12917_Philipsburg.tif_partial.tif',
    '597_RescUAV_12917_Philipsburg.tif_partial.tif',
    '604_RescUAV_12917_Philipsburg.tif_partial.tif',
    '17368_RescUAV_21917_Illidge.tif_partial.tif',
    '17369_RescUAV_21917_Illidge.tif_partial.tif',
    '194_RescUAV_21917_Illidge.tif_partial.tif',
    '1992_RescUAV_21917_Illidge.tif_partial.tif',
    '1993_RescUAV_21917_Illidge.tif_partial.tif',
    '2913_RescUAV_21917_Illidge.tif_partial.tif',
    '2916_RescUAV_21917_Illidge.tif_partial.tif',
    '325_RescUAV_21917_Illidge.tif_partial.tif',
    '383_RescUAV_27917_StJohns.tif_partial.tif',
    '3990_RescUAV_21917_Illidge.tif_partial.tif',
    '3991_RescUAV_21917_Illidge.tif_partial.tif',
    '3993_RescUAV_21917_Illidge.tif_partial.tif',
    '5490_RescUAV_27917_StJohns.tif_partial.tif',
    '5662_RescUAV_27917_StJohns.tif_partial.tif',
    '5670_RescUAV_27917_StJohns.tif_partial.tif',
    '5680_RescUAV_27917_StJohns.tif_partial.tif',
    '5681_RescUAV_27917_StJohns.tif_partial.tif',
    '5686_RescUAV_27917_StJohns.tif_partial.tif',
    '5882_RescUAV_27917_StJohns.tif_partial.tif',
    '5889_RescUAV_27917_StJohns.tif_partial.tif',
    '5962_RescUAV_27917_StJohns.tif_partial.tif',
    '5967_RescUAV_27917_StJohns.tif_partial.tif',
    '5970_RescUAV_27917_StJohns.tif_partial.tif',
    '5979_RescUAV_27917_StJohns.tif_partial.tif',
    '5978_RescUAV_27917_StJohns.tif_partial.tif',
    '6027_RescUAV_27917_StJohns.tif_partial.tif',
    '6034_RescUAV_27917_StJohns.tif_partial.tif',
    '6823_RescUAV_21917_Illidge.tif_partial.tif',
    '6826_RescUAV_21917_Illidge.tif_partial.tif',
    '6833_RescUAV_21917_Illidge.tif_partial.tif',
    '7952_RescUAV_21917_Illidge.tif_partial.tif',
    '7952_RescUAV_21917_Illidge.tif_partial',
    '913_RescUAV_21917_Illidge.tif_partial.tif',
    '957_RescUAV_21917_Illidge.tif_partial.tif',
    '973_RescUAV_21917_Illidge.tif_partial.tif',
];



// 2. extract category from file name
let images = [];
files.forEach((file) => {
    let match = file.match(/-(significant|partial|destroyed|none|unknown)[0-9]\.+/);
    if (!match) {
        console.log('NO MATCH FOR: ', file);
    }

    if (match) {
        match = match[1];

        images.push({
            'url': file,
            'tagIds': [tags.find((item) => item.name === match).id],
            // 'regions': [{'tagId': '{tagId}','left': 119.0,'top': 94.0,'width': 240.0,'height': 140.0}]
        });

        console.log(file, match);
    }
});

// 3. send to azure for training
const options = {
    method: 'POST',
    uri: `https://southcentralus.api.cognitive.microsoft.com/customvision/v2.0/Training/projects/027dfa39-5c51-4936-90a5-3bb3e2142081/images/urls`,
    headers: {
        'Training-Key': TRAINING_KEY
    },
    body: {
        'images': images,
        // 'tagIds': ['{tagId}']
    },
    json: true
};

rp(options)
    .then((response) => {
        console.log(response);
    })
    .catch((error) => {
        console.log(error);
    });


    