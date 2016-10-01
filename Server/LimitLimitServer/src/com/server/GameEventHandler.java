package com.server;

import java.util.logging.Logger;

import com.json.JSONObject;
import com.server.game.Card;
import com.server.game.GameManager;
import com.server.game.Player;

import io.netty.channel.Channel;

//this class will handle all the request / response logic and game protocol 
public class GameEventHandler {
	private final static Logger LOG = LoggerManager.GetLogger(GameEventHandler.class.getName());
	
	private GameManager gameManager; 
	
	private static int playerIdCounter = 0;
	private static int playerRegistretionCounter = 0;
	
	public GameEventHandler(GameManager _gameManager)
	{
		this.gameManager = _gameManager;
	}
	
	public int handleEvent(String _jsonRequest,Channel channel)
	{
		JSONObject jsonObject = new JSONObject(_jsonRequest);
		int Event = jsonObject.getInt("event");
		int playerId = -1;
		String userName =  jsonObject.getString("username");	        	
    	switch(Event)
    	{
        	case Config.LOGIN: 
        	{		        		 
        		Player newPlayer = setPlayerNewAttributes(userName,channel/*,Config.LOGIN_DONE*/);
        		setPlayerInPlayersContainer(newPlayer);
        		playerId = newPlayer.getId();
        		break;
        	}
        	default:
        		playerId = jsonObject.getInt("id");	
    	}
	        	
	    return playerId;
 	}
	
	
	public boolean dispatcheEvent(Player player, String _jsonRequest)
	{
		JSONObject jsonObject = new JSONObject(_jsonRequest);
		int Event = jsonObject.getInt("event");
		boolean bDone = false;
		switch(Event)
    	{
        	case Config.LOGIN: 
        	{		        		 
        		bDone = this.gameManager.getEventDispatcher().loginDone(player);
        		
        		break;
        	}
        	case Config.PLAY:
        	{
        		Card card = Card.fromJSON(jsonObject.getJSONObject("playedcard"));
        		bDone = this.gameManager.getEventDispatcher().playDone(player, card); 
        		break;
        	}
    	}
		
		return bDone;
	}
	
	
	
	 
	/*private int invokePlayEvent(JSONObject _jsonObject)
	{
		int activePlayerId  = _jsonObject.getInt("id");
		int currentPlayerID = this.gameManager.getPlayers().get(activePlayerId).getActiveplayerid();
		//validation of turn
		if(activePlayerId==currentPlayerID)
		{
			
			//find out who is the previous player
			int playerInx = getPreviousePlayerIndex(currentPlayerID);
			
			
			String currentPlayerCardId = this.gameManager.getPlayers().get(activePlayerId).getActivecardid();
			//check if the cards deck is active in there are cards in it
			if(this.gameManager.getCardsPlayDeck().size()>0)
			{
				String prevCardId = this.gameManager.getCardsPlayDeck().getFirst();
				//check which card has greater value
				int  prevCardValue = this.gameManager.getCardValueById(prevCardId);
				int  currentCardValue = this.gameManager.getCardValueById(currentPlayerCardId);
				//check if previous card is greater
				if(prevCardValue > currentCardValue)
				{
					
					//set the cards to the winner which is previous player
					this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().addLast(currentPlayerCardId);
					this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().addLast(prevCardId);
					//set as winner
					this.gameManager.getPlayerByIndex(playerInx).setWinner(playerInx);
					this.gameManager.getPlayerByIndex(playerInx).setWinnercards(currentPlayerCardId+"_"+prevCardId);
					this.gameManager.getPlayerByIndex(currentPlayerID).setWinner(playerInx);
					this.gameManager.getPlayerByIndex(currentPlayerID).setWinnercards(currentPlayerCardId+"_"+prevCardId);
					
					String currentCartId = this.gameManager.getPlayers().get(activePlayerId).getPlayerCards().getFirst();
					this.gameManager.getPlayers().get(activePlayerId).setActivecardid(currentCartId);
					
					String cardInDeck = this.gameManager.getCardsPlayDeck().getFirst();
					this.gameManager.getPlayerByIndex(playerInx).setDeckcard(cardInDeck);
					this.gameManager.getCardsPlayDeck().clear(); 
					
				}
				//check if current card is greater
				else if(prevCardValue < currentCardValue)
				{
					 
					 
					String prevPlayerCardId = this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().getFirst();
					this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().removeFirst();
					this.gameManager.getPlayers().get(currentPlayerID).getPlayerCards().addLast(prevPlayerCardId);
					
					//set as winner
					this.gameManager.getPlayerByIndex(playerInx).setWinner(playerInx);
					this.gameManager.getPlayerByIndex(playerInx).setWinnercards(currentPlayerCardId+"_"+prevPlayerCardId);
					this.gameManager.getPlayerByIndex(currentPlayerID).setWinner(playerInx);
					this.gameManager.getPlayerByIndex(currentPlayerID).setWinnercards(currentPlayerCardId+"_"+prevPlayerCardId);
					
					
					String currentCartId = this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().getFirst();
					this.gameManager.getPlayerByIndex(playerInx).setActivecardid(currentCartId);
					
					String cardInDeck = this.gameManager.getCardsPlayDeck().getFirst();
					this.gameManager.getPlayerByIndex(playerInx).setDeckcard(cardInDeck);
					this.gameManager.getCardsPlayDeck().clear();
					
					
				}
				else if(prevCardValue == currentCardValue)
				{
					 
					String PreviousePlayerCards[] = getWarCards(playerInx);
					String currentPlayerCards[] = getWarCards(currentPlayerID); 
					
					int  prevCardValue_4 = this.gameManager.getCardValueById(PreviousePlayerCards[3]);
					int  currentCardValue_4 = this.gameManager.getCardValueById(currentPlayerCards[3]);
					//check who is the winner 
					if(prevCardValue_4 > currentCardValue_4)
					{
						String result = CardsArrayToString(PreviousePlayerCards,currentPlayerCards);
						this.gameManager.getPlayerByIndex(playerInx).setWinner(1);
						this.gameManager.getPlayerByIndex(playerInx).setWinnercards(result);
						String currentCartId = this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().getFirst();
						this.gameManager.getPlayerByIndex(playerInx).setActivecardid(currentCartId);
						 
					}
					else if(prevCardValue_4 < currentCardValue_4)
					{
						String result = CardsArrayToString(currentPlayerCards,PreviousePlayerCards);
						this.gameManager.getPlayerByIndex(currentPlayerID).setWinner(1);
						this.gameManager.getPlayerByIndex(currentPlayerID).setWinnercards(result);
						String currentCartId = this.gameManager.getPlayerByIndex(currentPlayerID).getPlayerCards().getFirst();
						this.gameManager.getPlayerByIndex(currentPlayerID).setActivecardid(currentCartId);
					}
					else if(prevCardValue_4 == currentCardValue_4)
					{
						//TODO 
						int test =0;
					}
					this.gameManager.getCardsPlayDeck().clear();
				}
			}
			else
			{
				this.gameManager.getCardsPlayDeck().addFirst(currentPlayerCardId);
				this.gameManager.getPlayers().get(activePlayerId).getPlayerCards().removeFirst();
				String currentCartId = this.gameManager.getPlayers().get(activePlayerId).getPlayerCards().getFirst();
				this.gameManager.getPlayers().get(activePlayerId).setActivecardid(currentCartId);
				
				String cardInDeck = this.gameManager.getCardsPlayDeck().getFirst();
				this.gameManager.getPlayers().get(activePlayerId).setDeckcard(cardInDeck);
	        	 
				
			}	
			
			//Check if there are winners for this game
			int prevPlayerCardsSize = this.gameManager.getPlayerByIndex(playerInx).getPlayerCards().size();
			if(prevPlayerCardsSize==0)
			{
				//game is ended
				this.gameManager.getPlayerByIndex(playerInx).setEndgame(currentPlayerID);
				this.gameManager.getPlayerByIndex(currentPlayerID).setEndgame(currentPlayerID);
				
			}
		}
		else
		{
			activePlayerId =-1;
		}
		return activePlayerId;
	}*/
	
	private Player setPlayerNewAttributes(String _userName, Channel channel/*, int nextEvent*/)
	{
		Player newPlayer = new Player(channel);
		newPlayer.setUserName(_userName);
		int id = GenerateUniqueId(); 
		int count = getPlayerRegistretionCounter();
		newPlayer.setRegistertionNum(count);
		newPlayer.setId(id);
		//newPlayer.setEvent(nextEvent);
		return newPlayer;
	}
	
	private void setPlayerInPlayersContainer(Player _player)
	{
		this.gameManager.getPlayers().put(_player.getId(), _player);
	}
	
	private int GenerateUniqueId()
	{
		int id = GameEventHandler.playerIdCounter;
		GameEventHandler.playerIdCounter++;
		return id;
	}
	
	private int getPlayerRegistretionCounter()
	{
		int count = GameEventHandler.playerRegistretionCounter;
		GameEventHandler.playerRegistretionCounter++;
		return count;
	}

	public void beginGame() {
			gameManager.getEventDispatcher().beginGame();
	}
	 
}
