package com.server;

import java.util.logging.Logger;

import com.server.game.Card;
import com.server.game.GameManager;
import com.server.game.Player;

public class GameEventDispatcher implements GameEventInterface
{
	private final static Logger LOG = LoggerManager.GetLogger(GameEventHandler.class.getName());
	private GameManager gameManager; 
	private final GameResponseDispatcher responseDispatcher;
	
	public GameEventDispatcher(GameManager _gameManager)
	{
		this.gameManager = _gameManager;
		responseDispatcher = new GameResponseDispatcher(_gameManager);
	}
	
	public boolean loginDone(Player player)
	{
		// TODO check login
		responseDispatcher.ResponseDispatcheLoginDone(player);
		
		if(gameManager.canBegin())
		{
			new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		    			beginGame();
		            }
		        }, 
		        2000 
			);
		}
		
		return true;
	}
	
	public boolean playDone(Player player, Card card)
	{
		// TODO 
		if(player.haveCard(card))
		{
			player.setPlayedCard(card);
			player.setHasPlayed(true);
			
			responseDispatcher.ResponseDispatchePlayDone(player, card);
			
			return true;
		}
		else
		{
			responseDispatcher.ResponseDispatcheError("You dont have this card");
			return true;
		}
		
	}

	public boolean beginGame() 
	{
		this.gameManager.begin();
		responseDispatcher.ResponseDispatcheNextTurn();
		
		return true;
	}
	
	public boolean nextTurn(Player player) 
	{
		this.gameManager.nextTurn(player);
		responseDispatcher.ResponseDispatcheNextTurn();
		
		return true;
	}
}
