var TEXT_INPUT_FONT_NAME = "Thonburi";
var TEXT_INPUT_FONT_SIZE = 36;
var sceneIdx = -1;
var TEXT_FIELD_ERR = 1;
 
//--------------------------------------------------------------\
 
var LoginLayer = function(user, btn)
{
    this.textFieldUserName = null;
    this.buttonLogin = null;
    this.textErorrConnectedField = null;

    var login = this;

    this.init = function () {
        this.textFieldUserName = $(user);
        this.buttonLogin = $(btn);
        this.buttonLogin.click(function(e){
            e.preventDefault();
            login.loginGame();
        });
    };

    this.createErrorMsg = function () {
            this.textErorrConnectedField = new cc.TextFieldTTF("Error Connecting Server try again",
                                                            TEXT_INPUT_FONT_NAME,
                                                            TEXT_INPUT_FONT_SIZE+20);
            this.textErorrConnectedField.setTag(TEXT_FIELD_ERR);            
            this.textErorrConnectedField.x = cc.winSize.width / 2;
            this.textErorrConnectedField.y = cc.winSize.height / 2 +50;
            this.addChild(this.textErorrConnectedField,2);  
    };
    
    this.loginGame = function () {
        //remove error msg if any 
        /*if(this.getChildByTag(TEXT_FIELD_ERR)!==null)
        {
            this.removeChildByTag(TEXT_FIELD_ERR);
        }*/
        enterWorldScene = new EnterWorldScene();
                           enterWorldScene.run();
        //check login in the server 
        var txtUserName = this.textFieldUserName.val();
        var config = {
                    event:Events.LOGIN,
                    username:txtUserName
	    };
        var message = Encode(config);
        try {
            ws = new WebSocket("ws://localhost:8888/ws"); 
            ws.onopen = function() {
                                    
                    ws.send(message);
                
            };
            ws.onmessage = function (e) {
                console.log("app->srv.ws.onmessage():"+e.data);
                if(e.data!==null || e.data !== 'undefined')
                { 
                      var jsonFromClient = Decode(e.data);
                      if(jsonFromClient.event === Events.LOGIN_DONE)
                      {
                           enterWorldScene = new EnterWorldScene(jsonFromClient);
                      }
                }
            };
            ws.onclose = function (e) {
                    
            };
            ws.onerror = function (e) {
                  
            };
        } catch (e) {
            console.error('Sorry, the web socket at "%s" is un-available', url);
        }
    };

    this.init();
};

