package com.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.json.JSONArray;
import com.json.JSONObject;
import com.server.game.BlackCard;
import com.server.game.Card;
import com.server.game.GameManager;
import com.server.game.Player;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class GameResponseDispatcher {
	private final static Logger LOG = LoggerManager.GetLogger(GameEventHandler.class.getName());
	private GameManager gameManager; 
	
	public GameResponseDispatcher(GameManager _gameManager)
	{
		this.gameManager = _gameManager;
		 
	}
	
	public void setPlayerJson(Player player, int event, JSONObject json)
	{
		player.setPlayerJson(json, event);
		response(player);
	}
	
	public boolean ResponseDispatcheLoginDone(Player currentPlayer)
	{
		JSONObject currentPlayerJsonObj = setPlayerToJson(currentPlayer);
		JSONObject currentPlayerJsonObjForOthers = setPlayerToJson(currentPlayer);
		
    	JSONArray ArrayCurrentPlayers = new JSONArray();
    	
    	Iterator<Entry<Integer, Player>> it = this.gameManager.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        
	        if(currentPlayer.getId() != playerIt.getId())
	        {
	        	JSONObject playerJsonObjIt = setPlayerToJson(playerIt);
	        	
	        	JSONObject newPlayerForCurrent  = setPlayerToJson(playerIt);
	        	ArrayCurrentPlayers.put(newPlayerForCurrent);
	        	
	        	playerJsonObjIt.put("newplayer",currentPlayerJsonObjForOthers);
	        	
	        	setPlayerJson(this.gameManager.getPlayers().get(playerIt.getId()), Config.NEW_USER_LOGIN_DONE, playerJsonObjIt);
	        }
	    }
	    
	    currentPlayerJsonObj.put("players",ArrayCurrentPlayers);
	    setPlayerJson(currentPlayer, Config.LOGIN_DONE, currentPlayerJsonObj);
	    
		return true;
	}
	
	private JSONObject setPlayerToJson(final Player player)
	{	
		JSONObject _jsonPlayer = new JSONObject();
		_jsonPlayer.put("id",player.getId());
		_jsonPlayer.put("username", player.getUserName());
		_jsonPlayer.put("playedcard", setCardToJson(player.getPlayedCard()));
		_jsonPlayer.put("hasplayed", player.isHasPlayed());
		_jsonPlayer.put("score", player.getScore());
		 
		return _jsonPlayer;
	}
	
	private JSONObject setCardToJson(Card card)
	{	
		if(card != null)
		{
			JSONObject jsonCard = new JSONObject();
			jsonCard.put("id",card.getId());
			jsonCard.put("message", card.getMessage());
			 
			return jsonCard;
		}
		return null;
	}

	public boolean ResponseDispatcheNextTurn() 
	{
		Player winner = this.gameManager.getWinner();
		JSONObject winnerPlayerJsonObj = setPlayerToJson(winner);
		JSONObject winnerPlayerJsonObjForOthers = setPlayerToJson(winner);
		JSONObject blackCardJsonObj = cardToJson(this.gameManager.getBlackCard());
		winnerPlayerJsonObj.put("blackcard", blackCardJsonObj);
		
    	Iterator<Entry<Integer, Player>> it = this.gameManager.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        
	        if(winner.getId() != playerIt.getId())
	        {
	        	JSONObject playerJsonObjIt = setPlayerToJson(playerIt);
	        	
	        	playerJsonObjIt.put("winner",winnerPlayerJsonObjForOthers);
	        	playerJsonObjIt.put("blackcard", blackCardJsonObj);
	        	
	        	setPlayerJson(this.gameManager.getPlayers().get(playerIt.getId()), Config.NEW_TURN, playerJsonObjIt);
	        }
	    }
	    
	    setPlayerJson(winner, Config.NEW_TURN_WINNER, winnerPlayerJsonObj);
	    
		return true;
	}
	
	private void responseToClient(Player _player,String responseJson)
	{
		_player.getChannel().writeAndFlush(new TextWebSocketFrame(responseJson));
		LOG.severe("Sending to client id:"+String.valueOf(_player.getId())+" name:"+_player.getUserName()+" json:" +responseJson);
	}
	
	private void response(Player _player)
	{
		responseToClient(_player, _player.getPlayerJson());
	}

	private JSONObject cardToJson(Card card) {
		JSONObject cardJson = new JSONObject();
		cardJson.put("id", card.getId());
		cardJson.put("message", card.getMessage());
		return cardJson;
	}

	public boolean ResponseDispatchePlayDone(Player currentPlayer, Card card) 
	{
		JSONObject currentPlayerJsonObj = setPlayerToJson(currentPlayer);
		JSONObject currentPlayerJsonObjForOthers = setPlayerToJson(currentPlayer);
    	
    	Iterator<Entry<Integer, Player>> it = this.gameManager.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        
	        if(currentPlayer.getId() != playerIt.getId())
	        {
	        	JSONObject playerJsonObjIt = setPlayerToJson(playerIt);
	        	
	        	playerJsonObjIt.put("player",currentPlayerJsonObjForOthers);
	        	
	        	setPlayerJson(this.gameManager.getPlayers().get(playerIt.getId()), Config.USER_PLAY_DONE, playerJsonObjIt);
	        }
	    }
	    
	    setPlayerJson(currentPlayer, Config.PLAY_DONE, currentPlayerJsonObj);
	    
		return true;
	}

	public boolean ResponseDispatcheError(Player player, String message) 
	{
		// TODO Auto-generated method stub
		JSONObject currentPlayerJsonObj = setPlayerToJson(player);
		currentPlayerJsonObj.put("error", message);
		setPlayerJson(player, Config.ERROR, currentPlayerJsonObj);
		
		return true;
	}

	public boolean ResponseDispatcheAllPlaysDone() 
	{
		JSONArray ArrayPlays = new JSONArray();
		JSONArray ArrayPlaysForWinner = new JSONArray();
		
		Iterator<Entry<Integer, Player>> it1 = this.gameManager.getPlayers().entrySet().iterator();
	    while (it1.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it1.next();
	        Player playerIt = ((Player)pair.getValue());
	        
	        JSONObject play = this.setCardToJson(playerIt.getPlayedCard());
	        JSONObject playW = this.setCardToJson(playerIt.getPlayedCard());
	        if(play != null)
	        {
	        	JSONObject playerJsonObjIt = setPlayerToJson(playerIt);
		        play.put("player", playerJsonObjIt);
		        ArrayPlays.put(play);
		        ArrayPlaysForWinner.put(playW);
	        }
	    }
	    
		Iterator<Entry<Integer, Player>> it = this.gameManager.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        
        	JSONObject playerJsonObjIt = setPlayerToJson(playerIt);
        	if(playerIt.isWinner())
        		playerJsonObjIt.put("plays", ArrayPlaysForWinner);
        	else
        		playerJsonObjIt.put("plays", ArrayPlays);
        	
        	setPlayerJson(this.gameManager.getPlayers().get(playerIt.getId()), Config.ALL_PLAYS_DONE, playerJsonObjIt);
	    }
	    
		return true;
	}
}
