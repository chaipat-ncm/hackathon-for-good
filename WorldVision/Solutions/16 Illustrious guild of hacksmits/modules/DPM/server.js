var dale   = require ('dale');
var teishi = require ('teishi');

var fs = require ('fs');

var cberror = function (error) {
   process.stderr.write (error.toString ());
   process.exit (1);
}

fs.readFile ('input.json', 'utf8', function (error, data) {
   if (error) return cberror (error);

   data = JSON.parse (data);

   // XXX Mock data, to be replaced by CSV dataset inputs.

   var DATA = [
      {country: 'Bangladesh',  disaster: 'Flood',      probability: 0.4, intensity: 40},
      {country: 'Indonesia',   disaster: 'Flood',      probability: 0.2, intensity: 70},
      {country: 'Afghanistan', disaster: 'Earthquake', probability: 0.2, intensity: 70},
      {country: 'St. Lucia',   disaster: 'Cyclone',    probability: 0.9, intensity: 25},
   ];

   dale.do (DATA, function (v) {
      if (data.params.cycloneDelta) v.probability = v.probability + ((1 - v.probability) / data.params.cycloneDelta);
   });

   data.data = DATA;

   process.stdout.write (JSON.stringify (data));
});
