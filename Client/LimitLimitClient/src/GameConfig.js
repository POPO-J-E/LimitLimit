var WebSocket = WebSocket || window.WebSocket || window.MozWebSocket; 
var ws = null;

Events  = {
    HANDSHAKE_COMPLETE_SUCCESS : 1,
    LOGIN : 2,
    LOGIN_DONE : 3,
    LOGIN_FAIL : 4,
    NEW_USER_LOGIN_DONE : 5,
    DISCONNECT : 6,
    USER_DISCONNECTED : 7,
    PLAY : 8,
    PLAY_DONE : 9,
    USER_PLAY_DONE : 10,
    ALL_PLAYS_DONE : 11,
    CHOOSE_WINNER : 12,
    CHOOSE_WINNER_DONE : 13,
    WHINNER_CHOOSED : 14,
    NEW_TURN : 15,
    NEW_TURN_WINNER : 16,
    ERROR : 17,
};

States = 
{
    WAITING_FOR_PLAYERS : 1, 
    CAN_PLAY : 2, 
    WAITING_FOR_PLAYS : 3, 
    CAN_CHOOSE : 4, 
    WAITING_FOR_CHOOSE : 5, 
    WAITING_PAUSE : 6
};


var Encode = function(obj) {
       return JSON.stringify(obj);
   };
var Decode = function(obj) {
    return JSON.parse(obj);
};