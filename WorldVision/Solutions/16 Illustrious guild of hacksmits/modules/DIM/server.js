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

   var intensityFactors = {
      Earthquake: {
         affected: 400,
         damages:  600,
      },
      Flood: {
         affected: 250,
         damages:  300,
      },
      Cyclone: {
         affected: 150,
         damages:  250,
      },
   }

   var countries = {
      Bangladesh:  {population: 80,  development: 50},
      'St. Lucia': {population: 0.1, development: 55},
      Indonesia:   {population: 200, development: 60},
      Afghanistan: {population: 40,  development: 30},
   }

   dale.do (data.data, function (v) {
      v.affected = intensityFactors [v.disaster].affected * v.intensity * countries [v.country].population;
      v.damages  = intensityFactors [v.disaster].damages  * v.intensity * countries [v.country].development;
   });

   process.stdout.write (JSON.stringify (data));
});
