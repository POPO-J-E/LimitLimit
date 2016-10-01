package com.server.game;

public class BlackCard extends Card
{
	private int blanks;
	
	public BlackCard(int id, String message) {
		super(id, message);
		
		blanks = 1;
	}

}
