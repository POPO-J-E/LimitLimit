var WebSocket = WebSocket || window.WebSocket || window.MozWebSocket; 
var ws = null;

Events  = {
    HANDSHAKE_COMPLETE_SUCCESS:1,
    LOGIN:2,
    LOGIN_DONE:3,
    NEW_USER_LOGIN_DONE:4,
    PLAY:5,
    PLAY_DONE:6,
};


var Encode = function(obj) {
       return JSON.stringify(obj);
   };
var Decode = function(obj) {
    return JSON.parse(obj);
};