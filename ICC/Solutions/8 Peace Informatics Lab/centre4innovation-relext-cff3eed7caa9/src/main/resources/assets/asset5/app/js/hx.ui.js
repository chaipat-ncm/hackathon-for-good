/**
 * Simplified UI components.
 * Depends on jQuery and Metronic5.
 * Arvid Halma
 */

$.hx = $.hx || {}

$.hx.notify = function (message, type, icon, delay=5000) {
    $.notify({
        // options
        icon: icon,
        message: message,
        target: '_blank'
    },{
        // settings
        element: 'body',
        position: null,
        state: type,
        type: type,
        allow_dismiss: true,
        placement: {
            from: "top",
            align: "right"
        },
        offset: 20,
        delay: delay,
        url_target: '_blank',
        mouse_over: 'pause',
        animate: {
            enter: 'animated fadeInDown',
            exit: 'animated fadeOutUp'
        },
    });
}


$.hx.daterangepicker = function(element = "#m_daterangepicker", onUpdate = function(){} ){
    let toDate =  moment()
    let fromDate = moment().subtract('days', 29)

    $(element).daterangepicker({
        buttonClasses: "m-btn btn",
        applyClass: "btn-brand",
        cancelClass: "btn-secondary",
        startDate: fromDate,
        endDate: toDate,
        opens: ($('body').css('direction') === 'rtl' ? 'right' : 'left'),
        ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
            'Last 7 Days': [moment().subtract('days', 6), moment()],
            'Last 30 Days': [moment().subtract('days', 29), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
        },
    }, function(date1, date2, n) {
        fromDate = date1;
        toDate = date2;
        $(element + " .form-control").val(date1.format("YYYY-MM-DD") + " / " + date2.format("YYYY-MM-DD"))
        onUpdate()
    })

    $("#m_daterangepicker .form-control").val(fromDate.format("YYYY-MM-DD") + " / " + toDate.format("YYYY-MM-DD"))
}


$.hx.pagedaterangepicker = function(onUpdate = function(){} ) {
    const picker = $('#m_dashboard_daterangepicker');

    if (picker.length === 0) {
        return;
    }

    picker
        .addClass("m-subheader__daterange")
        .css('box-shadow', '0 3px 20px 0 rgba(113,106,202,.17)')
        .html(`<span class="m-subheader__daterange-label">
                    <span class="m-subheader__daterange-title"></span>
                    <span class="m-subheader__daterange-date m--font-brand"></span>
                </span>
                <a href="#" class="btn btn-sm btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--custom m-btn--pill">
                    <i class="la la-angle-down"></i>
                </a>`)

    let fromDate = $.hx.get('startDate', moment().subtract(6, 'days'));
    let toDate = $.hx.get('endDate', moment());

    function rangeUpdate(start, end, label) {
        // start = moment(start)
        // end = moment(end)
        let title = '';
        let range = '';

        if ((end - start) < 100 || label === 'Today') {
            title = 'Today:';
            range = start.format('MMM D');
        } else if (label === 'Yesterday') {
            title = 'Yesterday:';
            range = start.format('MMM D');
        } else {
            let now = moment();
            if (start.year() !== end.year()) {
                range = start.format('MMM D YYYY') + ' - ' + end.format('MMM D YYYY');
            } else {
                if (now.year() === start.year()) {
                    range = start.format('MMM D') + ' - ' + end.format('MMM D');
                } else {
                    range = start.format('MMM D') + ' - ' + end.format('MMM D YYYY');
                }
            }
        }

        picker.find('.m-subheader__daterange-date').html(range);
        picker.find('.m-subheader__daterange-title').html(title);
        $.hx.set('startDate', start);
        $.hx.set('endDate', end);
        onUpdate(start, end);
    }

    picker.daterangepicker({
        startDate: fromDate,
        endDate: toDate,
        opens: 'left',
        ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    }, rangeUpdate);

    rangeUpdate(fromDate, toDate, '');

}

$.hx.setCurrentPage = function(id){
    $(id).addClass('m-menu__item--active');
}

$.hx.toTable = function toTable(json, colKeyClassMap, rowKeyClassMap){
    let tab;
    if(typeof colKeyClassMap === 'undefined'){
        colKeyClassMap = {};
    }
    if(typeof rowKeyClassMap === 'undefined'){
        rowKeyClassMap = {};
    }

    const newTable = '<table class="table table-bordered table-condensed table-striped" />';
    if($.isArray(json)){
        if(json.length === 0){
            return '[]'
        } else {
            const first = json[0];
            if($.isPlainObject(first)){
                tab = $(newTable);
                const row = $('<tr />');
                tab.append(row);
                $.each( first, function( key, value ) {
                    row.append($('<th />').addClass(colKeyClassMap[key]).text(key))
                });

                $.each( json, function( key, value ) {
                    const row = $('<tr />');
                    $.each( value, function( key, value ) {
                        row.append($('<td />').addClass(colKeyClassMap[key]).html($.hx.toTable(value, colKeyClassMap, rowKeyClassMap)))
                    });
                    tab.append(row);
                });

                return tab;
            } else if ($.isArray(first)) {
                tab = $(newTable);

                $.each( json, function( key, value ) {
                    const tr = $('<tr />');
                    const td = $('<td />');
                    tr.append(td);
                    $.each( value, function( key, value ) {
                        td.append($.hx.toTable(value, colKeyClassMap, rowKeyClassMap));
                    });
                    tab.append(tr);
                });

                return tab;
            } else {
                return json.join(", ");
            }
        }

    } else if($.isPlainObject(json)){
        tab = $(newTable);
        $.each( json, function( key, value ) {
            tab.append(
                $('<tr />')
                    .append($('<th style="width: 20%;"/>').addClass(rowKeyClassMap[key]).text(key))
                    .append($('<td />').addClass(rowKeyClassMap[key]).html($.hx.toTable(value, colKeyClassMap, rowKeyClassMap))));
        });
        return tab;
    } else if (typeof json === 'string') {
        if(json.slice(0, 4) === 'http'){
            return '<a target="_blank" href="'+json+'">'+json+'</a>';
        }
        return json;
    } else {
        return ''+json;
    }
};

$.hx.scrollIntoView = function(selector, offset=0, behavior='smooth'){
    function getOffset(el) {
        if(!el)
            return {left: 0, top: 0}

        el = el.getBoundingClientRect();
        return {
            left: el.left + window.scrollX,
            top: el.top + window.scrollY
        }
    }
    let elOffset = getOffset($(selector)[0]);
    window.setTimeout(function() {
        window.scrollTo({
            top: elOffset.top + offset,
            left: elOffset.left,
            behavior: behavior
        });
    }, 200)
}