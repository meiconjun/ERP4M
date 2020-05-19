/**
 * webSocket相关操作js
 */


/**
 * 获取websocket连接地址
 */
function getWebSocketUrl() {
    let rootObj = window.location;//应用根路径
    // websocketurl
    let urlPath = rootObj.host + "/" + rootObj.pathname.split("/")[1] + "/websocket.do";
    if (window.location.protocol == 'http:') {
        return 'ws://' + urlPath;
    } else {
        return 'wss://' + urlPath;
    }
    // return "ws://" + window.location.host + "/websocket";
}

/**
 * 断开websocket连接
 * @param ws
 */
function disconnectWebSocket(ws) {
    if (ws != null) {
        ws.close();
        ws = null;
    }
}

/**
 * 建立websocket连接
 * @param ws
 */
function connectToWebSocket(ws) {
    // 连接建立后的回调函数
    ws.onopen = function () {
        console.log("websocket连接成功！");
    };
    // 接收消息的回调函数
    ws.onmessage = function (event) {

    }
    // 连接断开的回调函数
    ws.onclose = function (event) {
        console.error("websocket连接断开")
    }
}
