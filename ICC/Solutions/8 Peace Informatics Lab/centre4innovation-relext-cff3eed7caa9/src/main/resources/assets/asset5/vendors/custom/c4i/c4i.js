/**
 * Some general and metronic-specific utilities.
 * Created by arvid on 18-4-16.
 */

$.c4i = $.c4i || {};

// Core logic
$.c4i.getFromDate = function(){
    var fromDate = Cookies.get("fromDate");
    return fromDate ? moment(fromDate) : moment().subtract('days', 29);
};

$.c4i.setFromDate = function(date){
    Cookies.set("fromDate", $.type(date) === 'string' ? date : date.format('YYYY-MM-DD'), {path: '/'});
};

$.c4i.getToDate = function(){
    var fromDate = Cookies.get("toDate");
    return fromDate ? moment(fromDate) : moment();
};

$.c4i.setToDate = function(date){
    Cookies.set("toDate", $.type(date) === 'string' ? date : date.format('YYYY-MM-DD'), {path: '/'});
};

$.c4i.getUrlParameter = function(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

$.c4i.get = function(url, data)  {
    return $.ajax({
        url: url,
        data: data,
        headers: { 'Authorization': 'Bearer ' + 123 },
    });
};

//setup ajax error handling
$.c4i.loginRedirect = function(){$.ajaxSetup({
    error: function (event) {
        if (event.status == 401 || event.status == 403) {
            // alert("Sorry, your session has expired. Please login again to continue");
            window.location.href = "/login.html";
        }
    }
})};

$.c4i.logout = function() {
  // http://tuhrig.de/basic-auth-log-out-with-javascript/

  // To invalidate a basic auth login:
  //
  // 	1. Call this logout function.
  //	2. It makes a GET request to an URL with false Basic Auth credentials
  //	3. The URL returns a 401 Unauthorized
  // 	4. Forward to some "you-are-logged-out"-page
  // 	5. Done, the Basic Auth header is invalid now

  jQuery.ajax({
    type: "GET",
    url: "/api/v1/system/logout",
    async: false,
    username: "logmeout",
    password: "123456",
    headers: { "Authorization": "Basic xxx" }
  })
    .done(function(){
      // If we don't get an error, we actually got an error as we expect an 401!
    })
    .fail(function(){
      // We expect to get an 401 Unauthorized error! In this case we are successfully
      // logged out and we redirect the user.
      window.location = "/api/v1/ui";
    });

  return false;

}

// Gui stuff
$.c4i.submitOnEnter = function() {
    $('input.form-control').keypress(function (event) {
        if (event.keyCode == '13') { //jquery normalizes the keycode

            event.preventDefault(); //avoids default action
            $(this).closest('button').trigger('click');
            $(this).parents('.portlet').find('button').first().trigger('click');
            // or $(this).closest('form').submit();
        }
    });
};

$.c4i.toTable = function toTable(json, colKeyClassMap, rowKeyClassMap){
    if(typeof colKeyClassMap == 'undefined'){
        colKeyClassMap = {};
    }
    if(typeof rowKeyClassMap == 'undefined'){
        rowKeyClassMap = {};
    }

    var newTable = '<table class="table table-bordered table-condensed table-striped" />';
    if($.isArray(json)){
        if(json.length == 0){
            return '[]'
        } else {
            var first = json[0];
            if($.isPlainObject(first)){
                var tab = $(newTable);
                var row = $('<tr />');
                tab.append(row);
                $.each( first, function( key, value ) {
                    row.append($('<th />').addClass(colKeyClassMap[key]).text(key))
                });

                $.each( json, function( key, value ) {
                    var row = $('<tr />');
                    $.each( value, function( key, value ) {
                        row.append($('<td />').addClass(colKeyClassMap[key]).html(toTable(value, colKeyClassMap, rowKeyClassMap)))
                    });
                    tab.append(row);
                });

                return tab;
            } else if ($.isArray(first)) {
                var tab = $(newTable);

                $.each( json, function( key, value ) {
                    var tr = $('<tr />');
                    var td = $('<td />');
                    tr.append(td);
                    $.each( value, function( key, value ) {
                        td.append(toTable(value, colKeyClassMap, rowKeyClassMap));
                    });
                    tab.append(tr);
                });

                return tab;
            } else {
                return json.join(", ");
            }
        }

    } else if($.isPlainObject(json)){
        var tab = $(newTable);
        $.each( json, function( key, value ) {
            tab.append(
                $('<tr />')
                    .append($('<th style="width: 20%;"/>').addClass(rowKeyClassMap[key]).text(key))
                    .append($('<td />').addClass(rowKeyClassMap[key]).html(toTable(value, colKeyClassMap, rowKeyClassMap))));
        });
        return tab;
    } else if (typeof json == 'string') {
        if(json.slice(0, 4) == 'http'){
            return '<a target="_blank" href="'+json+'">'+json+'</a>';
        }
        return json;
    } else {
        return ''+json;
    }
};

$.c4i.createDateRangePicker = function(onchange, parentDiv, dontSetGlobalVars){
  parentDiv = parentDiv || '#dashboard-report-range';
  const drp = $(parentDiv).daterangepicker({
    "ranges": {
      'Today': [moment(), moment()],
      'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
      'Last 7 Days': [moment().subtract('days', 6), moment()],
      'Last 30 Days': [moment().subtract('days', 29), moment()],
      'This Month': [moment().startOf('month'), moment().endOf('month')],
      'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
    },
    "locale": {
      "format": "YYYY-MM-DD",
      "separator": " - ",
      "applyLabel": "Apply",
      "cancelLabel": "Cancel",
      "fromLabel": "From",
      "toLabel": "To",
      "customRangeLabel": "Custom",
      "daysOfWeek": ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
      "monthNames": ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
      "firstDay": 1
    },
    "startDate": $.c4i.getFromDate(),
    "endDate": $.c4i.getToDate(),
    opens: (App.isRTL() || parentDiv ? 'right' : 'left'), // if parentdiv, then more likely to be in form, instead of top-right on page
  }, function (start, end, label) {
    if(!dontSetGlobalVars) {
      $.c4i.setFromDate(start);
      $.c4i.setToDate(end);
    }
    onchange(start, end);
  });
  $(parentDiv).show();
  return drp;

};

$.c4i.datatable = function(element, columns, data, sorting, columnDefs, scrollX) {
    return $(element).DataTable({
        dom: "<'row' <'col-md-12'B>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable
        data: data,
        columns: columns,
        responsive: !scrollX,
        deferRender: true,
        destroy: true,
        lengthMenu: [[10, 50, 100, -1], [10, 50, 100, "All"]],
        buttons: [
            {extend: 'print', className: 'btn grey-salt btn-outline'},
            {extend: 'copy', className: 'btn yellow-haze btn-outline'},
            {extend: 'excel', className: 'btn green-meadow btn-outline' },
            {extend: 'csv', className: 'btn blue btn-outline'},
            /*            {
             text: 'Reload',
             className: 'btn yellow btn-outline',
             action: function (e, dt, node, config) {
             //dt.ajax.reload();
             updateTransxTable()
             }
             }*/
        ],
        aaSorting: sorting ? sorting : [[ 0, "desc" ]],
        columnDefs: columnDefs,
        scrollX: scrollX
    });
};


$.c4i.removeAccents = function(s){
    let r = s.toLowerCase();
    r = r.replace(new RegExp("\\s", 'g'),"");
    r = r.replace(new RegExp("[àáâãäå]", 'g'),"a");
    r = r.replace(new RegExp("æ", 'g'),"ae");
    r = r.replace(new RegExp("ç", 'g'),"c");
    r = r.replace(new RegExp("[èéêë]", 'g'),"e");
    r = r.replace(new RegExp("[ìíîï]", 'g'),"i");
    r = r.replace(new RegExp("ñ", 'g'),"n");
    r = r.replace(new RegExp("[òóôõö]", 'g'),"o");
    r = r.replace(new RegExp("œ", 'g'),"oe");
    r = r.replace(new RegExp("[ùúûü]", 'g'),"u");
    r = r.replace(new RegExp("[ýÿ]", 'g'),"y");
    r = r.replace(new RegExp("\\W", 'g'),"");
    return r;
};

$.c4i.uppercaseFirst = function(s) {
    return s.charAt(0).toUpperCase() + s.slice(1);
};

// http://stackoverflow.com/questions/149055/how-can-i-format-numbers-as-money-in-javascript
$.c4i.formatMoney = function(n, nDecimals, decimalSeparator, thousandsSeparator){
    var c = nDecimals == undefined ? 2 : nDecimals,
        d = decimalSeparator == undefined ? "." : decimalSeparator,
        t = thousandsSeparator == undefined ? "," : thousandsSeparator,
        s = n < 0 ? "-" : "",
        i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

$.c4i.formatDateTime = function(datetimestr){
    if(!datetimestr)
        return null;
    return datetimestr.replace(/([+-]\d\d:\d\d)/, '<span class="font-grey-silver">$1</span>');
};

$.c4i.zeroPad = function(num, size) {
    var s = num+"";
    while (s.length < (size || 2)) s = "0" + s;
    return s;
};

$.c4i.hideCalendar = function () {
  $('.page-bar').css('visibility', 'hidden');
};


$.c4i.saveAs = function saveAs(uri, filename) {
    var link = document.createElement('a');
    if (typeof link.download === 'string') {
        document.body.appendChild(link); // Firefox requires the link to be in the body
        link.download = filename;
        link.href = uri;
        link.click();
        document.body.removeChild(link); // remove the link when done
    } else {
        location.replace(uri);
    }
};

$.c4i.saveMaps = function(){
    // http://stackoverflow.com/questions/24046778/html2canvas-does-not-work-with-google-maps-pan

    $('#saveMapBtn').click(function () {
        var element = $("#gmap_marker");

        //get transform value
        var transform=$(".gm-style>div:first>div").css("transform");
        var comp=transform.split(",");    //split up the transform matrix
        var mapleft=parseFloat(comp[4]);  //get left value
        var maptop=parseFloat(comp[5]);   //get top value
        $(".gm-style>div:first>div").css({ //get the map container. not sure if stable
            "transform":"none",
            "left":mapleft,
            "top":maptop
        });

        $(".gmnoprint").css({visibility:"hidden"});

        html2canvas(element, {
            useCORS: true,
            logging: false,
            onrendered: function(canvas) {
                // var image = canvas.toDataURL("image/png")
                // window.location.href = image; // it will save locally
                var dataUrl= canvas.toDataURL('image/png').replace("image/png", "image/octet-stream");  // here is the most important part because if you dont replace you will get a DOM 18 exception.
                // window.location.href = dataUrl; //for testing I never get window.open to work
                $.c4i.saveAs(dataUrl, 'c4i-map.png');

                $(".gm-style>div:first>div").css({
                    left:0,
                    top:0,
                    "transform":transform
                });
                $(".gmnoprint").css({visibility:"inherit"});
            }
        });

    });
};

$.c4i.pageInit = function(item){
    // show nav menu
    // $('.page-sidebar').html($.c4i.menu);
    $(item).addClass('active open').find('a').append('<span class="selected" />');

    // init logout button
    $('#logoutBtn').click($.c4i.logout);

    // make enter key submit the form
    $.c4i.submitOnEnter();

    // attach save map buttons
    $.c4i.saveMaps();
};

$.c4i.mapInfoLinks = function(retailerId){
    return '<div class="pull-right"><a href="http://www.dsms-lebanon.org/#/main/merchant/lookup/'+retailerId+'" class="btn btn-info btn-xs">DSMS</a> ' +
        '<a href="#/'+retailerId+'" class="btn btn-info btn-xs">Shop tracker</a></div>'
};