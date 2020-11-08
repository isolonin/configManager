function startChecking(button) {
    const $button = $(button)
    const $span = $button.find('span.ui-icon');
    $span.attr('prev-classes', $span.attr('class'));
    $span.addClass('fa-spinner fa-spin');
    $button.addClass('disabled');
}

function openSocket() {
    let socket = new SockJS('/check-template');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/client/update-check-status', function () {
            updateCheckingsTable();
            updateCheckButton();
        });
    });
}

(function ($) {
    $(document).ready(function () {
        openSocket();
    });
})(jQuery);