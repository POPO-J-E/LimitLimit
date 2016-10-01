var TEXT_INPUT_FONT_NAME = "Thonburi";
var TEXT_INPUT_FONT_SIZE = 36;
var TEXT_INPUT_FONT_SIZE_PLAYER = 20;

var Player = function(_id,_username,_score)
{
    this.init(_id,_username,_score);
}


Player.prototype = {
    id:null,
    username:null,
    state:null,
    hand:null,
    score:null,
    isWinner:null,
    playedCard:null,
    winnercards:null,
    registertionnum:null,
    hasPlayed:null,
    playedObject:null,

    htmlObject:null,
    textFieldUserName:null,
    textFieldScore:null,

    init: function(_id,_username,_score){        
        this.id = _id;
        this.username = _username;
        this.score = _score;
        this.hasPlayed = false;
    },    
    setObject:function(_object)
    {
        this.htmlObject = _object;

        this.textFieldUserName = this.htmlObject.find('.name');
        this.textFieldScore = this.htmlObject.find('.score');
        this.playedObject = this.htmlObject.find('.played');

        this.setPlayerNameCaption(this.username);
        this.setScore(this.score);
        this.setHasPlayed(this.hasPlayed);
    },
    setPlayerNameCaption:function(_name)
    {
        this.textFieldUserName.html(_name);
    },
    setScore:function(_score)
    {
        this.score = _score;
        this.textFieldScore.html(_score);
    },
    setHasPlayed:function(_played)
    {
        if(this.hasPlayed != _played)
        {
            this.hasPlayed = _played;

            if(_played)
            {
                this.playedObject.removeClass('glyphicon-remove');
                this.playedObject.addClass('glyphicon-ok');
            }
            else
            {
                this.playedObject.addClass('glyphicon-remove');
                this.playedObject.removeClass('glyphicon-ok');
            }
        }

    },
    update:function(jsonObj)
    {
        this.state = jsonObj.state;
        this.setScore(jsonObj.score);
    }
};
 


