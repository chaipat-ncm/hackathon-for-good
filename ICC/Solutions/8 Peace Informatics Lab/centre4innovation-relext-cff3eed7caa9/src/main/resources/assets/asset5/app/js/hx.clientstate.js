/**
 * localStorage wrapper.
 * Arvid Halma
 */

$.hx = $.hx || {}


$.hx.getUrlParam = function (name) {
    return new URL(window.location.href).searchParams.get(name);
}

$.hx.set = function (name, value) {
    if(value._isAMomentObject){
        value = value.format()
    } else if(Array.isArray(value) ||typeof value === 'object'){
        value = JSON.stringify(value)
    }
    localStorage.setItem(name, value);
}

$.hx.get = function (name, defaultValue) {

    // 1. try url param
    let item = $.hx.getUrlParam(name)
    // 2. try localStorage
    if(item == null)
        item = localStorage.getItem(name);
    if(item === null){
        // 3. store and use defaultValue
        $.hx.set(name, defaultValue)
        return defaultValue;
    }
    try {
        return JSON.parse(item)
    } catch (e) {
        const RE_ISO_DATE = /\d{4}-[01]\d-[0-3]\dT?[0-2]\d:[0-5]\d(?::[0-5]\d(?:.\d{1,6})?)?(?:([+-])([0-2]\d):?([0-5]\d)|Z)/
        if(RE_ISO_DATE.test(item))
            return moment(item)
        return item
    }
}

//setup ajax error handling
$.hx.loginRedirect = function(){$.ajaxSetup({
    error: function (event) {
        if (event.status === 401 || event.status === 403) {
            // alert("Sorry, your session has expired. Please login again to continue");
            window.location.href = "/api/v1/ui";
        }
    }
})};

$.hx.logout = function() {
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


