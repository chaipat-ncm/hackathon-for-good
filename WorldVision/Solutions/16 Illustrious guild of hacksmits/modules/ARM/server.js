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

   var countries = {
      Bangladesh:  {development: 50, preparedness: 65},
      'St. Lucia': {development: 55, preparedness: 40},
      Indonesia:   {development: 60, preparedness: 45},
      Afghanistan: {development: 30, preparedness: 50},
   }

   var costmapping = {
      affected: 20,
      damages:  25,
   }

   dale.do (data.data, function (v) {
      v.totalCost = v.affected * costmapping.affected + v.damages * costmapping.damages;
      if (data.params.oilprice) v.totalCost = Math.round (v.totalCost * data.params.oilprice);

      v.aidRequired = Math.round (v.totalCost * 100 / ((countries [v.country].development + countries [v.country].preparedness) / 2));
   });

   process.stdout.write (JSON.stringify (data));
});
