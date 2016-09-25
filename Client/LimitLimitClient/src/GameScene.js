//var spriteFrameCache = cc.spriteFrameCache;
var size = null;
var MENU_TAG = 1;
var CENTER_DECK = 2;
var CARD = 3;
var TEXT_INPUT_FONT_SIZE_PLAYER = 20;

var EnterWorldScene = function(data)
{
    this.init(data);
    this.onEnter();
};

EnterWorldScene.prototype = {
    cardDeckSprite:null,
    sprite:null,
    listener1:null,
    jsonData:null,
    textFieldUserNameCaption:null,
    currentPlayer:null,
    otherPlayers:[],
    playerList:null,

    init:function (_jsondata) {
        this.jsonData = _jsondata;
        //after succesful login we want to take control on massages coming from server
        //so we attache this new callback function to the websocket onmessage
        ws.onmessage = this.ongamestatus.bind(this);
        ws.onclose = this.onclose.bind(this);
        ws.onerror = this.onerror.bind(this);
          
        return true;
    },
     
    onEnter:function () 
    { 
        var userName = this.jsonData.username;

        this.textFieldUserNameCaption = $('');
        this.playerList = $('#usersContainer ul');
        
        this.eventHandler(this.jsonData.event);
     },
    eventHandler:function(event)
    {
        switch (event) {
            case Events.LOGIN_DONE:
            {
              this.setupCurrentPlayer();
              break;
            }
            case Events.NEW_USER_LOGIN_DONE:
            {
              this.setupOtherPlayerS();
              break;
             
            }
            case Events.PLAY_DONE:
            {
              //this.setPlayState();
              break;
             
            }
        }
        this.setTurnMessage();
    },
    setMessage:function(_message)
    {
        this.textFieldUserNameCapton.html(_message); 
        console.log("CaptionMessage->"+_message);
    },
    setTurnMessage:function()
    {
       var userName = this.jsonData.username;
       var activePlayerId = this.jsonData.activeplayerid;
       if(activePlayerId === this.currentPlayer.id)
       {
           this.setMessage("Hello "+userName+" Its your turn"); 
       }
       else
       {
           this.setMessage("Hello "+userName); 
       }
       
    },
    onCallbackMoveTo:function (nodeExecutingAction,player) {
        //console.log("nodeExecutingAction id:"+nodeExecutingAction.playerid+"  :nodeExecutingAction.x:"+nodeExecutingAction.x+" nodeExecutingAction.y:"+nodeExecutingAction.y);
        //var activePlayerId = this.jsonData.activeplayerid;
        //if(activePlayerId !== this.currentPlayer.id)
        //{
            this.currentPlayer.updatePlayerNumberOfCardsCaption(this.jsonData.numcardsleft);
            this.otherPlayers[0].updatePlayerNumberOfCardsCaption(this.jsonData.players[0].numcardsleft);
        //}
        //else
        //{
            //TODO this fix this hard coded position getter
            //this.otherPlayers[0].updatePlayerNumberOfCardsCaption(this.jsonData.players[0].numcardsleft);
            //this.currentPlayer.updatePlayerNumberOfCardsCaption(this.jsonData.numcardsleft);
        //}
         
    },
    setPlayState:function()
    {
         
        this.currentPlayer.setNewCardById(this.jsonData.activecardid);
        this.updatePlayer(this.currentPlayer,this.jsonData); 
        if(this.jsonData.players.length>0)
        {
            for(var i=0;i<this.jsonData.players.length;i++)
            {
                if(this.jsonData.players[i].event === Events.PLAY_DONE)
                {
                    this.otherPlayers[i].setNewCardById(this.jsonData.players[i].activecardid);
                    this.updatePlayer(this.otherPlayers[i],this.jsonData.players[i]);
                }
            } 
        }
        //handle animation
        var pos = null;
        var activePlayerId = this.jsonData.activeplayerid;
        if(activePlayerId !== this.currentPlayer.id)
        {
            pos = this.currentPlayer.getPosition();
        }
        else
        {
            //TODO this fix this hard coded position getter
            pos = this.otherPlayers[0].getPosition();
        }
         
        var cardInDeck = this.jsonData.deck;
        this.animateCard(cardInDeck,pos);
        
    },
    animateCard:function(_cardInDeckId,_pos)
    {
        var cardName =  cards[_cardInDeckId];
        this.cardDeckSprite = new cc.Sprite("#"+cardName);
        this.cardDeckSprite.attr({
                x: _pos.x,//(cc.winSize.width / 2) ,
                y: _pos.y//(cc.winSize.height / 2)
            });
        this.addChild(this.cardDeckSprite,1,CENTER_DECK); //TODO handel removeble when not needed by tag name
        var posMid = cc.p(size.width/2,size.height/2); 
        var action = cc.sequence(
                    cc.moveTo(0.5, posMid),
                    cc.callFunc(this.onCallbackMoveTo,this,this.cardDeckSprite));
        this.cardDeckSprite.runAction(action);          
    },
    invokeTurn:function()
    {
       if(this.currentPlayer.id == this.currentPlayer.activeplayerid)
       {
          var config = {
                        event:Events.PLAY,
                        username:this.currentPlayer.username,
                        id:this.currentPlayer.id,
          };  
          var message = Encode(config);
          ws.send(message);
       }
       else
       {
          console.log("GameScene->invokeTurn() not its turn:"+this.currentPlayer.id); 
       }
    },
    setupCurrentPlayer:function()
    {
        this.currentPlayer = new Player(this.jsonData.id,this.jsonData.username, this.jsonData.activecardid);        
        this.updatePlayer(this.currentPlayer,this.jsonData);      
        
        this.addPlayer(this.currentPlayer, true);

        if(this.jsonData.players.length>0)
        {
            for(var i=0;i<this.jsonData.players.length;i++)
            {
                if(this.jsonData.players[i].event === Events.NEW_USER_LOGIN_DONE)
                {
                    this.setupOtherPlayer(i);
                }
            } 
        }     
    },
    setupOtherPlayerS:function()
    {
        if(this.jsonData.players.length>0)
        {
            for(var i=0;i<this.jsonData.players.length;i++)
            {
                if(this.jsonData.players[i].event === Events.LOGIN_DONE)
                {
                    this.setupOtherPlayer(i);
                }
            } 
        }
    },
    setupOtherPlayer:function(inx)
    {
        var player = new Player(this.jsonData.players[inx].id,
                                            this.jsonData.players[inx].username,
                                            this.jsonData.players[inx].activecardid);  
        this.otherPlayers[inx] = player;
        this.updatePlayer(this.otherPlayers[inx],this.jsonData.players[inx]);
        
        this.addPlayer(player);
    },
    updatePlayer:function(_player,jsonObj)
    {
        _player.activeplayerid = jsonObj.activeplayerid;
        _player.activecardid =  jsonObj.activecardid;
        _player.event =  jsonObj.event;
        _player.registertionnum =  jsonObj.registertionnum; 
        _player.winner =  jsonObj.winner; 
        _player.winnercards =  jsonObj.winnercards; 
        _player.numcardsleft =  jsonObj.numcardsleft; 
    },
    addPlayer:function(_player, current = false){
        $model = this.playerList.find('.model');
        $player = $model.clone();
        $player.removeClass('model');
        if(current)
            $player.addClass('current');
        _player.setObject($player);
        this.playerList.append($player);
    },
    ongamestatus:function(e) {
          console.log("GameScene->.ws.onmessage():"+e.data);
          if(e.data!==null || e.data !== 'undefined')
          { 
              this.jsonData = Decode(e.data);
              this.eventHandler(this.jsonData.event);
         }
     }
     ,
    
    onclose:function (e) {

    },
    onerror:function (e) {

    }
      
};  
