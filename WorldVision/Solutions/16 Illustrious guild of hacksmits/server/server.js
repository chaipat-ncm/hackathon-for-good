var CONFIG = require ('./config.js');
var SECRET = require ('./secret.js');

var fs     = require ('fs');
var spawn  = require ('child_process').spawn;
Error.stackTraceLimit = Infinity;

var dale   = require ('dale');
var teishi = require ('teishi');
var lith   = require ('lith');
var cicek  = require ('cicek');
var redis  = require ('redis').createClient ({db: CONFIG.redisdb});
var giz    = require ('giz');
var hitit  = require ('hitit');
giz.redis  = redis;

var bcrypt = require ('bcryptjs');

redis.keyscan = function (match, cb, cursor, keys) {
   if (! cursor) cursor = 0;
   if (! keys)   keys   = {};
   redis.scan (cursor, 'MATCH', match, function (error, result) {
      if (error) return cb (error);
      cursor = result [0];
      dale.do (result [1], function (key) {
         keys [key] = true;
      });
      if (cursor !== '0') return redis.keyscan (match, cb, cursor, keys);
      cb (null, dale.keys (keys));
   });
}

var type = teishi.t, log = teishi.l, reply = cicek.reply, stop = function (rs, rules) {
   return teishi.stop (rules, function (error) {
      reply (rs, 400, {error: error});
   });
}

var hit = function (command, cb) {

   var output = {out: '', err: ''};

   var proc = spawn (command [0], command.slice (1));

   var wait = 3;

   var done = function () {
      if (--wait > 0) return;
      if (output.err !== '' || output.code !== 0) return cb (output);
      cb (null, output);
   }

   dale.do (['stdout', 'stderr'], function (v) {
      proc [v].on ('data', function (chunk) {
         output [v.replace ('std', '')] += chunk;
      });
      proc [v].on ('end', done);
   });

   proc.on ('error', function (error) {
      output.err += error + ' ' + error.stack;
      done ();
   });
   proc.on ('exit',  function (code, signal) {output.code = code; output.signal = signal; done ()});
}

// *** MODULE INTERFACES ***

var modules = dale.obj (['DPM', 'DIM', 'ARM', 'APM'], function (m) {
   return [m, function (data, cb) {
      fs.writeFile ('input.json', m === 'DPM' ? JSON.stringify ({params: data}) : data, function (error) {
         if (error) return cb (error);
         hit (['node', '../modules/' + m + '/server.js'], function (error, data) {
            if (error) return cb (error);
            cb (null, data.out);
         });
      });
   }];
});

// *** ROUTES ***

var routes = [

   // *** STATIC ASSETS ***

   ['get', '/lib/(*)', cicek.file, ['../client/lib']],
   ['get', 'client.js', cicek.file, ['../client']],
   ['get', '/', reply, lith.g ([
      ['!DOCTYPE HTML'],
      ['html', [
         ['head', [
            ['meta', {charset: 'utf-8'}],
            ['meta', {name: 'viewport', content: 'width=device-width,initial-scale=1'}],
            ['title', 'Model AID'],
            ['link', {rel: 'stylesheet', href: 'https://fonts.googleapis.com/css?family=Montserrat'}],
            ['link', {rel: 'stylesheet', href: 'https://fonts.googleapis.com/css?family=Kadwa'}],
            dale.do (['ionicons.min', 'pure-min'], function (v) {
               return ['link', {rel: 'stylesheet', href: 'lib/' + v + '.css'}];
            })
         ]],
         ['body', [
            dale.do (['gotoB.min'], function (v) {
               return ['script', {src: 'lib/' + v + '.js'}];
            }),
            ['script', 'var COOKIENAME = \'' + CONFIG.cookiename + '\';'],
            ['script', {src: 'client.js'}]
         ]]
      ]]
   ])],

   ['post', 'run', function (rq, rs) {

      var b = rq.body;

      if (stop (rs, [
         ['body', b, 'object'],
         function () {return [
         ]},
      ])) return;

      var cberror = function (error) {
         reply (rs, 500, {error: error});
      }

      var sequence = ['DPM', 'DIM', 'ARM', 'APM'], counter = 0;

      var Data = b;

      var next = function () {
         modules [sequence [counter++]] (Data, function (error, data) {
            if (error) return reply (rs, 500, {error: error});
            Data = data;
            if (modules [sequence [counter]]) return next ();
            fs.unlink ('input.json', function (error) {
               if (error) return reply (rs, 500, {error: error});
               reply (rs, 200, JSON.parse (data));
            });
         });
      }
      next ();
   }],

];

// *** LAUNCH SERVER ***

cicek.options.cookieSecret = SECRET.cookie;
cicek.options.log.body = function (log) {
   if (log.requestBody && log.requestBody.password) return false;
   return true;
}

cicek.apres = function (rs) {
   if (rs.log.requestBody && rs.log.requestBody.password) {
      rs.log.requestBody.password = '';
   }
   cicek.Apres (rs);
}

cicek.cluster ();

cicek.listen ({port: CONFIG.port}, routes);
