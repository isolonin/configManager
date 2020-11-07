function startChecking(link) {
    let $i = $(link).find('i');
    $i.addClass('fa-spinner fa-spin');
    $i.removeClass('fa-plug');
}

function openSocket() {
    let socket = new SockJS('/check-template');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/client/update-table', function () {
            updateTable();
        });
    });
}

(function ($) {
    $(document).ready(function () {
        openSocket();
    });
})(jQuery);