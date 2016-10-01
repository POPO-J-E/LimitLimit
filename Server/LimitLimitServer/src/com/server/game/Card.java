package com.server.game;

import com.json.JSONObject;

public class Card 
{
	private int id;
	
	private String message;
	
	public Card(int id, String message)
	{
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static Card fromJSON(JSONObject jsonObject) {
		Card card = new Card(jsonObject.getInt("id"), jsonObject.getString("message"));
		return card;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Card)
		{
			return ((Card)o).getId() == this.getId();
		}
		else
			return false;
	}
}
