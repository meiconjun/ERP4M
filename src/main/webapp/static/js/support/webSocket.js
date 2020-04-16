/**
 * webSocket相关操作js
 */


/**
 * 建立websocket连接
 */
function connectToWebSocket() {
    let rootPath = window.location.href;//应用根路径
    let urlPath = rootPath.substring(rootPath.lastIndexOf("/"), rootPath.length) + "/websocket";
}