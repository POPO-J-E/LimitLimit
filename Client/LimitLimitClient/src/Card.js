var TEXT_INPUT_FONT_NAME = "Thonburi";
var TEXT_INPUT_FONT_SIZE = 36;
var TEXT_INPUT_FONT_SIZE_PLAYER = 20;

var Card = function(_game, _id, _message, _white = true)
{
    this.init(_game, _id, _message, _white);
}

Card.prototype = {
    id:null,
    message:null,
    white:null,
    htmlObject:null,
    game:null,
    tapedTwice:null,
    selected:false,

    init: function(_game, _id, _message, _white = true){      
        this.game = _game;  
        this.id = _id;
        this.message = _message;
        this.white = _white;
    },    
    setHtml:function (_object) {        
        this.htmlObject = _object;

        var card = this;
        this.htmlObject.dblclick(function(){
            card.game.applyClick(card);
        });

        this.htmlObject.on("touchstart", this.tapHandler);
    },
    setHtmlChoice:function (_object) {        
        this.htmlObject = _object;

        var card = this;
        this.htmlObject.click(function(){
            card.game.applyChoiceClick(card);
        });
    },
    tapHandler:function (event) 
    {
        if(!this.tapedTwice) {
            this.tapedTwice = true;
            setTimeout( function() { this.tapedTwice = false; }, 300 );
            return false;
        }
        event.preventDefault();
        //action on double tap goes below
        this.game.applyClick(this);
    },
    toArray:function()
    {
        var data = {};
        data['id'] = this.id;
        data['message'] = this.message;
        return data;
    },
};
 


