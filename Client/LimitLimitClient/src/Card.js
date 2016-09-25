var TEXT_INPUT_FONT_NAME = "Thonburi";
var TEXT_INPUT_FONT_SIZE = 36;
var TEXT_INPUT_FONT_SIZE_PLAYER = 20;

var Card = function(_game, _id, _text, _white = true)
{
    this.ctor(_game, _id, _text, _white);
}

Card.prototype = {
    id:null,
    text:null,
    white:null,
    htmlObject:null,
    game:null,

    ctor: function(_game, _id, _text, _white){      
        this.game = _game;  
        this.id = _id;
        this.text = _text;
        this.white = _white;
    },    
    setHtml:function (_object) {        
        this.htmlObject = _object;
        
    },
    
};
 


