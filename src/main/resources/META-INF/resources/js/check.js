function openSocket() {
    const socket = new SockJS('/check-template');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/client/update-check-status', function (resp) {
            if (resp.body) updateButtons();
            updateAfterCheck();
        });
        stompClient.subscribe('/client/update-terminal-connection', function (resp) {
            if (resp.body) {
                console.log('updateTerminal');
                updateTerminal();
            } else {
                console.log(resp);
            }
        });
    });
}

(function ($) {
    $(document).ready(function () {
        openSocket();
    });
})(jQuery);