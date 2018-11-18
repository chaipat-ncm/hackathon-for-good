(function () {

   // *** SETUP ***

   var dale = window.dale, teishi = window.teishi, lith = window.lith, c = window.c, B = window.B;
   var type = teishi.t, log = teishi.l;

   // *** INITIALIZATION OF STATE/DATA ***

   B.do ({from: {ev: 'initialize'}}, 'set', 'State', {});
   B.do ({from: {ev: 'initialize'}}, 'set', 'Data',  {});

   window.State = B.get ('State'), window.Data = B.get ('Data');

   // *** NAVIGATION ***

   B.listen ('change', 'hash', function (x) {
      var path = window.location.hash.replace ('#/', '').split ('/');
      B.do (x, 'set', ['State', 'view'],    path [0]);
      B.do (x, 'set', ['State', 'subview'], path [1]);
   });

   B.listen ('change', ['State', '*'], function (x) {
      if (x.path [1] !== 'view' && x.path [1] !== 'subview') return;
      var view = B.get ('State', 'view');
      var cookie = c.cookie () ? c.cookie () [COOKIENAME] : undefined;
      // XXX To be changedwhen auth is added
      if (! cookie && view !== 'auth') return B.do (x, 'set', ['State', 'view'], 'main');
      if (cookie   && view !== 'main') return B.do (x, 'set', ['State', 'view'], 'main');
      window.location.hash = ['#', B.get ('State', 'view'), B.get ('State', 'subview')].join ('/');
   });

   window.addEventListener ('hashchange', function () {
      B.do ({from: {ev: 'hashchange'}}, 'change', 'hash')
   });

   // *** INITIALIZATION ***

   c.ready (function () {
      B.do ({from: {ev: 'ready'}}, 'change', 'hash');
      B.mount ('body', Views.base ({from: {ev: 'ready'}}));
   });

   // *** HELPERS ***

   var H = window.H = {};

   H.logout = function (x) {
      c.ajax ('get', 'auth/logout');
      B.eventlog = [];
      B.do (x, 'set', 'State', {});
      B.do (x, 'set', 'Data',  {});
      window.State = B.get ('State'), window.Data = B.get ('Data');
      c.cookie (false);
      B.do (x, 'set', ['State', 'view'], 'auth');
   }

   H.authajax = function (x, m, p, h, b, cb) {
      x = H.from (x, {ev: 'authajax', method: m, path: p, headers: h, body: b});
      return c.ajax (m, p, h, b, function (error, rs) {
         if (error && error.status === 403) {
            H.logout (x);
            return B.do (x, 'notify', 'red', 'Your session has expired. Please login again.');
         }
         if (error) console.log (error.responseText);
         cb (error, rs);
      });
   }

   H.if = function (cond, then, Else) {
      return cond ? then : Else;
   }

   H.from = function (x, o) {
      x.from.unshift (o);
      return x;
   }

   H.spaceh = function (cols) {
      var COLS = 40;
      return 100 * cols / COLS + 'vw';
   }

   H.spacev = function (number, lineHeight) {
      var TYPELINEHEIGHT = 1.5;
      return number * (lineHeight || TYPELINEHEIGHT) + 'rem';
   }

   H.fontSize = function (value, ratio, base) {
      var TYPEBASE = 1, TYPERATIO = 1.2;
      return Math.pow (ratio || TYPERATIO, value) * (base || TYPEBASE) + 'rem';
   }

   // *** VIEWS ***

   var Views = {};

   H.css = {
      blue: '#4562FF',
      gray1: '#3A3A3A',
      gray2: '#484848',
      gray3: '#8B8B8B',
      gray4: '#DEDEDE',
      gray5: '#F2F2F2',
      gray6: '#FBFBFB',
      font: 'Montserrat, sans-serif',
   }

   Views.base = function (x) {
      return B.view (x, ['State', 'view'], function (x, view) {
         return [
            ['style', [
               ['html', {
                  'font-size': 17,
                  'font-family': 'Montserrat, sans-serif',
               }],
               ['.button', {
                  cursor: 'pointer',
                  'font-family': 'Montserrat, sans-serif',
                  'background-color': H.css.blue,
                  color: 'white',
                  border: 'none',
                  'font-weight': 'bold',
                  'border-radius': 25,
                  'padding-top': 13,
                  'padding-bottom': 14,
                  'padding-left': 25,
                  'padding-right': 23
               }],
               ['.bold', {
                  'font-weight': 'bold',
               }],
               ['.float', {
                  float: 'left',
               }],
               ['p', {
                  'font-size': 14,
               }],
               ['.pointer', {
                  cursor: 'pointer',
               }],
               ['i.inline', {
                  'margin-right': 5,
               }],
               ['input', {
                  height: 41,
                  'border-radius': 20,
                  border: 0,
                  'background-color': H.css.gray5,
                  'padding-left': 16,
                  'line-height': 0.7,
                  'font-family': H.css.font,
               }],
            ]],
            Views.notify (x),
            Views [view] ? Views [view] (x) : undefined,
         ];
      });
   }

   // *** NOTIFY ***

   Views.notify = function (x) {
      return B.view (x, ['State', 'notify'], {listen: [
         ['notify', '*', function (x, message, notimeout) {
            if (B.get ('State', 'notify', 'timeout')) clearTimeout (B.get ('State', 'notify', 'timeout'));
            B.do (x, 'set', ['State', 'notify'], {color: x.path [0], message: message});
            if (! notimeout) B.do (x, 'set', ['State', 'notify', 'timeout'], setTimeout (function () {
               B.do (x, 'rem', 'State', 'notify');
            }, 3000));
         }],
      ]}, function (x, notify) {
         if (! notify) return;
         var colormap = {red: '#ff0033'};
         return [
            ['style', [
               ['div.notify', {
                  position: 'fixed',
                  'bottom, left': 0,
                  margin: '0 auto',
                  color: 'white',
                  border: 'solid 4px ' + (colormap [notify.color] || notify.color),
                  'background-color': '#333333',
                  height: '1.6em',
                  width: 1,
                  'z-index': '2',
                  padding: '0.5em',
                  opacity: notify ? 1 : 0,
                  'text-align': 'center',
                  'font-size': H.fontSize (1),
               }]
            ]],
            ['div', B.ev ({class: 'notify bold'}, ['onclick', 'rem', 'State', 'notify']), notify.message],
         ];
      });
   }

   // *** MAIN VIEW ***

   Views.main = function (x) {

      var routes = [
      ];

      return B.view (x, ['State', 'subview'], {listen: routes, ondraw: function (x) {
         if (['browse', 'upload'].indexOf (B.get ('State', 'subview')) === -1) B.do (x, 'set', ['State', 'subview'], 'browse');
      }}, function (x, subview) {
         return [
            Views [subview] ? Views [subview] (x) : undefined,
         ];
      });
   }

   // *** BROWSE VIEW ***

   Views.browse = function (x) {
      return [
         ['style', [
            ['div.left', {
               width: H.spaceh (7.0),
               'padding-left': H.spaceh (1),
               height: 1,
            }],
            ['div.center', {
               width: H.spaceh (24),
               'padding-left': H.spaceh (3),
            }],
            ['.button', {
               'font-size': H.fontSize (0),
               'padding-left, padding-right': 15,
            }, [
               ['i', {
                  'margin-right': 5,
                  color: 'white',
                  'font-weight': 'bold',
                  'font-size': H.fontSize (1),
                  'vertical-align': 'baseline',
               }],
            ]],
         ]],
         ['br'], ['br'],
         B.view (['Data', 'model'], {listen: [
            ['run', 'model', function (x) {
               var t = Date.now ();
               H.authajax (x, 'post', 'run', {}, {params: B.get ('State', 'params') || {}}, function (error, data) {
                  if (error) {
                     console.log (error);
                     return B.do (x, 'notify', 'red', 'Alas! There was an error running the model.');
                  }
                  B.do (x, 'notify', 'green', 'Model run successfully in ' + (Date.now () - t) + ' ms.');
                  B.do (x, 'set', ['Data', 'model'], data.body);
               });
            }],
         ], ondraw: function (x) {
            if (! B.get ('Data', 'model')) B.do (x, 'run', 'model');
         }}, function (x, model) {
            if (! model) return;
            var COLUMNS = ['country', 'disaster', 'probability', 'intensity', 'affected', 'damages', 'totalCost', 'aidRequired', 'need'];
            return ['div', {class: 'pure-g'}, [
               ['div', {style: 'padding-left: 10px', class: 'pure-u-3-5'}, [
                  ['h3', 'Expected disasters & impact'],
                  ['table', {class: 'pure-table pure-table-striped'}, [
                     ['tr', dale.do (COLUMNS, function (v) {return ['th', v]})],
                     dale.do (model.data, function (v) {
                        return ['tr', dale.do (COLUMNS, function (key) {
                           return ['td', v [key]];
                        })];
                     }),
                  ]],
                  ['br'],
                  ['br'],
                  ['br'],
                  ['button', B.ev ({class: 'button'}, ['onclick', 'run', 'model']), [['i', {class: 'ion-ios-plus-outline'}], 'Run model']],
               ]],
               ['div', {class: 'pure-u-1-5'}, [
                  ['br'],
                  ['br'],
                  ['br'],
                  ['br'],
                  ['br'],
                  ['br'],
               ]],
            ]];
         }),
      ];
   }

}) ();
