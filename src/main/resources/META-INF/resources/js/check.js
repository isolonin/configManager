function startChecking(button) {
    const $button = $(button)
    const $span = $button.find('span.ui-icon');
    $span.attr('prev-classes', $span.attr('class'));
    $span.addClass('fa-spinner fa-spin');
    $button.addClass('disabled');
}

function openSocket() {
    const socket = new SockJS('/check-template');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/client/update-check-status', function () {
            try {
                updateCheckingsTable();
            }catch (e){}
            try {
                updateCheckButton();
            }catch (e){}
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