var TEXT_INPUT_FONT_NAME = "Thonburi";
var TEXT_INPUT_FONT_SIZE = 36;
var TEXT_INPUT_FONT_SIZE_PLAYER = 20;

var Player = function(_id,_username,_activecardid)
{
    this.ctor(_id,_username,_activecardid);
}


Player.prototype = {
    id:null,
    username:null,
    event:null,
    activecardid:null,
    activeplayerid:null,
    registertionnum:null,
    winner:null,
    winnercards:"",
    numcardsleft:null,
    spriteSize:null,
    htmlObject:null,
    textFieldUserName:null,

    ctor: function(_id,_username,_activecardid){        
        this.id = _id;
        this.username = _username;
        this.activecardid = _activecardid;
    },    
    setObject:function(_object)
    {
        this.htmlObject = _object;
        this.setPlayerNameCaption(this.username);
    },
    setPlayerNameCaption:function(_name)
    {
        this.textFieldUserName = this.htmlObject.find('.name');
        this.textFieldUserName.html(_name);
    },    
};
 


