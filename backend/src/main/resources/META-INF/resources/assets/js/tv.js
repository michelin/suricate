// Global variable to indicate if the current page is already loaded
var page_loaded = false;

$(function() {
    $(window).on('load', function () {
        page_loaded = true;
    });
    // Application websocket events
    var PROJECT_EVENT = {
        DISCONNECT: 'DISCONNECT',
        WIDGET: 'WIDGET'
    };

    var USER_EVENT = {
        DISCONNECT: 'DISCONNECT',
        DISPLAY_NUMBER: 'DISPLAY_NUMBER'
    };

    // Connection issue with backend
    var ws_connection_error = false;

    // Gridstack configuration
    var options = {
        width: config.grid_width,
        cellHeight: config.widget_height,
        verticalMargin: 10
    };

    // init gridstack
    $('.grid-stack').gridstack(options);

    /**
     * Method used to handle connection issues
     */
    function handleConnectionError(){
        // Force reload on error
        if (ws_connection_error){
            setTimeout(function () {
                reloadPage();
            }, 5000);
        }
    }

    /**
     * Method used to reload page
     */
    function reloadPage(){
        var xhr = $.get(location.href, function(data, textStatus, xhr){
            if(xhr.state() == "resolved" && xhr.getResponseHeader("Dashboard") != undefined){
                location.reload();
            } else {
                setTimeout(function () {
                    reloadPage(location.href)
                }, 2000);
            }
        });
        xhr.fail(function (xhr, textStatus, error){
            setTimeout(function () {
                reloadPage(location.href)
            }, 2000);
        });
    }

    /**
     * Method used to handle project events
     * @param data the data to manage
     */
    function handleProjectEvent(data){
        var body = JSON.parse(data.body);

        if (body.type === PROJECT_EVENT.DISCONNECT) { // Disconnect event
            window.location.replace(config.context_path+"/tv");
        } else if (body.type === PROJECT_EVENT.WIDGET) { // Widget event
            var errorMsg = '';
            if (body.content.error) {
                errorMsg = $("#error-template").html();
            } else if (body.content.warning) {
                errorMsg = $("#warning-template").html();
            }
            $(".widget-"+body.content.projectWidgetId).html(errorMsg + body.content.html);
        } else {
            reloadPage();
        }
    }

    /**
     * Method used to handle user events
     * @param data the event data
     */
    function handleUserEvent(data){
        var body = JSON.parse(data.body);
        var screenCode = $("#screen-code");
        screenCode.show();
        if (body.type === USER_EVENT.DISPLAY_NUMBER){
            setTimeout(function () {
                screenCode.hide();
            }, 10000);
        } else if (body.type === USER_EVENT.DISCONNECT) {
            window.location.replace(config.context_path+"/tv");
        } else {
            screenCode.hide();
        }
    }

    // Start websocket
    function startWs() {
        var socket = new SockJS(config.context_path+'/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            handleConnectionError();
            if (config.token.length === 0) {
                stompClient.subscribe('/user/'+config.number+'/queue/register', function (register) {
                    window.location.replace(register.body);
                });
            } else {
                // register project events
                stompClient.subscribe('/user/'+config.token+'/queue/live', function (data) {
                    handleProjectEvent(data);
                });
                stompClient.subscribe('/user/'+config.token+'-'+config.number+'/queue/unique', function (data) {
                    handleUserEvent(data)
                });
            }
        });

        // Check on close event on websocket
        socket.onclose = function () {
            ws_connection_error = true;
            $("#server-connection-error").show();
            //try to reconnect in 5 seconds
            setTimeout(function () {
                startWs()
            }, 5000);
        };
    }
    startWs();

});