function handleLoginRequest(xhr, status, args) {
    if (args.validationFailed || args.failed) {
        PF('addDialog').jq.effect("shake", {times: 5}, 100);
    } else {
        PF('addDialog').hide();
    }
}