package com.server;

import com.server.game.Card;
import com.server.game.Player;

public interface GameEventInterface 
{
	public boolean loginDone(Player player);
	public boolean playDone(Player player, Card card);
}
