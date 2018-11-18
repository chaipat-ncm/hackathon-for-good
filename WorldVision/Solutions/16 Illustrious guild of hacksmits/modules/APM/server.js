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

   var totalAffected = dale.acc (data.data, 0, function (a, b) {return a.affected + b.affected});

   dale.do (data.data, function (v) {
      v.need = v.aidRequired / v.totalCost;
      if (data.params.immediate) v.need = v.need * 0.8 + 0.2 * data.params.immediate * (v.affected / totalAffected) * (100 / data.data.length);
   });

   process.stdout.write (JSON.stringify (data));

});
