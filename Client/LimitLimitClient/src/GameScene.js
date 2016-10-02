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
    jsonData:null,
    textFieldUserNameCaption:null, //html
    currentPlayer:null,
    otherPlayers:{},
    playerList:null, //html

    hand:{},
    whiteHand:null, //html

    blackCard:null,
    blackCardHtml:null, //html

    selectedCard:null,
    target:null, //html
    sender:null, //html

    canPlay:true,

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

        this.textFieldUserNameCaption = $('#infoGame');
        this.playerList = $('#usersContainer ul');

        this.whiteHand = $('#whitehand');
        this.blackCardHtml = $('#BlackCard');

        this.target = $('#DragAndDrop');
        this.sender = $('#sender');
        var game = this;
        this.sender.click(function(){
            game.sendCard();
        })

        $( "#Menu" ).click(function() {
          $('#usersContainer').addClass('show');
          $('.fa-bars').hide();
        });

        $( "#Close" ).click(function() {
          $('#usersContainer').removeClass('show');
           $('.fa-bars').show();
        });

        this.enablePlays(false);
        
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
              this.setupNewPlayer();
              break;
            }
            case Events.NEW_TURN:
            {
              this.setupNewTurn();
              break;
            }
            case Events.NEW_TURN_WINNER:
            {
              this.setupNewTurnWinner();
              break;
            }
            case Events.PLAY_DONE:
            {
              this.onPlayDone();
              break;
            }
            case Events.USER_PLAY_DONE:
            {
              this.onUserPlayDone();
              break;
            }
            case Events.ALL_PLAYS_DONE:
            {
              this.onAllPlaysDone();
              break;
             
            }
        }
        this.setTurnMessage();
    },
    setMessage:function(_message)
    {
        this.textFieldUserNameCaption.html(_message); 
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
        this.currentPlayer = new Player(this.jsonData.id,this.jsonData.username, this.jsonData.score);        
        
        this.addPlayer(this.currentPlayer, true);
        this.updatePlayer(this.currentPlayer,this.jsonData);

        if(this.jsonData.players.length>0)
        {
            for(var i=0;i<this.jsonData.players.length;i++)
            {
                this.setupOtherPlayer(i);
            } 
        }     
    },
    setupOtherPlayer:function(inx)
    {
        var player = new Player(this.jsonData.players[inx].id,
                                            this.jsonData.players[inx].username,
                                            this.jsonData.players[inx].score);  
        
        this.addPlayer(player);
        this.updatePlayer(this.otherPlayers[inx],this.jsonData.players[inx]);
    },
    setupNewPlayer:function()
    {
        var newPlayer = new Player(this.jsonData.newplayer.id,this.jsonData.newplayer.username, this.jsonData.newplayer.score);
        
        this.addPlayer(newPlayer);
        this.updatePlayer(newPlayer,this.jsonData);
    },
    updatePlayer:function(_player,jsonObj)
    {
        _player.update(jsonObj);
        /*_player.activeplayerid = jsonObj.activeplayerid;
        _player.activecardid =  jsonObj.activecardid;
        _player.event =  jsonObj.event;
        _player.registertionnum =  jsonObj.registertionnum; 
        _player.winner =  jsonObj.winner; 
        _player.winnercards =  jsonObj.winnercards; 
        _player.numcardsleft =  jsonObj.numcardsleft;*/ 
    },
    findAndUpdatePlayer:function(jsonObj)
    {
        var id = jsonObj.id;
        var player = this.otherPlayers[id];
        this.updatePlayer(player, jsonObj);
        return player;
    },
    addPlayer:function(_player, current = false)
    {
        $model = this.playerList.find('.model');
        $player = $model.clone();
        $player.removeClass('model');
        if(current)
            $player.addClass('current');
        else
            this.otherPlayers[_player.id] = _player;
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
     },

    setupNewTurn:function()
    {
        /*var newPlayer = new Player(this.jsonData.newplayer.id,this.jsonData.newplayer.username, this.jsonData.newplayer.score);
        
        this.addPlayer(newPlayer);
        this.updatePlayer(newPlayer,this.jsonData);*/
        this.setupTurn();
        this.enablePlays(true);
    },
    setupNewTurnWinner:function()
    {
        this.setupTurn();
    },
    setupTurn:function()
    {
        this.setupHand();
        this.setupBlackCard();
    },
    setupHand:function()
    {
        '<div class="whiteCard"></div>'
        if(this.jsonData.hand.length>0)
        {
            this.hand = {};
            this.whiteHand.html('');

            for(var i=0;i<this.jsonData.hand.length;i++)
            {
                this.addCardToHand(i);
            } 
        }     
    },
    addCardToHand:function(i)
    {
        var card = new Card(this, this.jsonData.hand[i].id, this.jsonData.hand[i].message);
        this.hand[card.id] = card;

        var cardHtml = $('<div class="whiteCard"></div>').html(card.message);
        card.setHtml(cardHtml);
        this.whiteHand.append(cardHtml);
    },
    setupBlackCard:function()
    {
        var card = new Card(this, this.jsonData.blackcard.id, this.jsonData.blackcard.message, false);
        this.blackCard = card;

        this.blackCardHtml.html(card.message);
    },
    applyClick:function(card)
    {
        if(this.canPlay)
        {
            if(this.target.hasClass("empty")){
                card.htmlObject.appendTo(this.target);
                this.target.removeClass("empty");
                this.target.addClass("full");
            }
            else{
                this.selectedCard.htmlObject.appendTo(this.whiteHand);
                card.htmlObject.appendTo(this.target);
            }
            this.selectedCard = card;
        }
    },
    sendCard:function()
    {
        if(this.canPlay)
        {
            if(this.selectedCard != null)
            {
                this.sendMessage("Selected a card.");
                var data = {};
                data['playedcard'] = this.selectedCard.toArray();
                this.sendPacket(Events.PLAY, data);
                this.enablePlays(false);
            }
            else
                this.sendMessage("Select a card before.");
        }
        else
        {
            this.sendMessage("You can not play.");
        }
    },
    sendMessage:function(message)
    {
        //TODO
        //alert(message);
        console.log(message);
    },
    sendPacket(event, data)
    {
        data['event'] = event;
        data['id'] = this.currentPlayer.id;
        data['username'] = this.currentPlayer.username;
        var message = Encode(data);
        alert(message);
        try {
            ws.send(message);
        } catch (e) {
            console.error('Sorry, the web socket at "%s" is un-available', url);
        }
    },
    onclose:function (e) {

    },
    onerror:function (e) {

    },
    enablePlays:function(_bool)
    {
        
        if(_bool && this.canPlay == false)
        {
            this.whiteHand.removeClass('cantplay');
            this.canPlay = true;
        }
        else if(!_bool && this.canPlay != false)
        {
            this.whiteHand.addClass('cantplay');
            this.canPlay = false;
        }
    },
    onPlayDone:function()
    {
        this.sendMessage('on play done');
        this.updatePlayer(this.currentPlayer,this.jsonData); 
    },
    onUserPlayDone:function()
    {
        this.sendMessage('on user play done');
        var player = this.findAndUpdatePlayer(this.jsonData.player);
    },
    onAllPlaysDone:function()
    {
        this.sendMessage("all plays done");
        
    },
};  
