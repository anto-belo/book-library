function getRequestParam(paramName) {
    let requestParams = location.search.substr(1).split('&');
    for (let i = 0; i < requestParams.length; i++) {
        if (requestParams[i].startsWith(paramName)) {
            return requestParams[i].split('=')[1];
        }
    }
    return null;
}

function clearChildren(node) {
    while (node.firstChild) {
        node.removeChild(node.lastChild);
    }
}

function dateNow(plusMonths) {
    let date = new Date();
    if (plusMonths !== undefined) {
        date.setMonth(date.getMonth() + +plusMonths);
    }
    let month = date.getMonth() + 1;
    if (month < 10) {
        month = '0' + month;
    }
    return date.getFullYear() + '-' + month + '-' + date.getDate();
}
