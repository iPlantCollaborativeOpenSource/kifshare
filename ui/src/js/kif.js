function trimURLPath (pathname) {
    var split_paths = pathname.split("/");
    var new_path_array = split_paths.slice(0, split_paths.length - 1);
    return new_path_array.join("/");
}

function curlableURL() {
    var a = document.createElement('a');
    a.href = document.URL;

    var retval = "";
    retval = retval + a.protocol + "//";
    retval = retval + a.hostname;

    if (a.port !== 'undefined' && a.port !== '80' && a.port !== null && a.port !== '') {
        retval = retval + ":" + a.port;
    }

    retval = retval + trimURLPath(a.pathname);
    
    return retval; 
}

$(document).ready(function() {
    $("#irods-avus-data").dataTable({
        "bJQueryUI" : true,
        "sPaginationType" : "full_numbers",
        "aoColumns" : [
            {"sWidth" : "315px"},
            {"sWidth" : "315px"},
            {"sWidth" : "315px"}
        ]
    });

    //Object containing key-value pairs that can be used with Mustache.
    var ticket_info = JSON.parse($('#ticket-info-map').text());
    ticket_info.url = curlableURL();

    var last_mod_date = new Date(Number($('#lastmod').text()));
    $('#lastmod').text(last_mod_date.toString());

    //Grab the Mustache template strings from the hidden divs.
    var wget_template = ticket_info.wget_template;
    var curl_template = ticket_info.curl_template;
    var iget_template = ticket_info.iget_template;

    var wget_command = _.unescape(Mustache.render(wget_template, ticket_info));
    var curl_command = _.unescape(Mustache.render(curl_template, ticket_info));
    var iget_command = _.unescape(Mustache.render(iget_template, ticket_info));
    
    $('#clippy-irods-wrapper').text(iget_command);
    $('#clippy-wget-wrapper').text(wget_command);
    $('#clippy-curl-wrapper').text(curl_command);
    $('#irods-command-line').val(iget_command);
    $('#curl-command-line').val(curl_command);
    $('#wget-command-line').val(wget_command);
    
    $('.clippy-curl').clippy({clippy_path : 'flash/clippy.swf'});
    $('.clippy-wget').clippy({clippy_path : 'flash/clippy.swf'});
    $('.clippy-irods').clippy({clippy_path : 'flash/clippy.swf'});

    $('table').css("border-bottom", "2px rgb(210,210,210) solid");
    $('table').css("border-left", "1px rgb(210,210,210) solid");
    $('table').css("border-right", "1px rgb(210,210,210) solid");
    $('table').css("border-top", "1px rgb(210,210,210) solid");
});
