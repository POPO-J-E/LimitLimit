package com.server.game;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.json.JSONArray;
import com.json.JSONObject;
import com.server.GameServerMain;
import com.server.LoggerManager;

import io.netty.channel.Channel;

public class Player 
{
	private final static Logger LOG = LoggerManager.GetLogger(GameServerMain.class.getName());
	
	public static enum State {WAITING_FOR_PLAYERS(1), CAN_PLAY(2), WAITING_FOR_PLAYS(3), 
		CAN_CHOOSE(4), WAITING_FOR_CHOOSE(5), WAITING_PAUSE(6);
		private int id;
		State(int id)
		{
			this.id = id;
		}
		
		public int id()
		{
			return id;
		}
	};

	private int id;
	private String userName;
	private List<Card> hand;
	private State state;
	//Session channel
	private Channel channel;
	private boolean isWinner;
	private Card playedCard;
	private int registertionNum;
	private int score;
	private boolean hasPlayed;
	
	private String playerJson;
	
	public Player()
	{
		this.channel = null;
		Init();
	}
	
	public Player(Channel _channel)
	{
		this.channel = _channel;
		Init(); 
	}

	private void Init()
	{
		this.setHand(new LinkedList<Card>());
		this.setState(State.WAITING_FOR_PLAYERS);
		this.score = 0;
		hasPlayed = false;
	}
	 
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public Channel getChannel() {
		return channel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getRegistertionNum() {
		return registertionNum;
	}

	public void setRegistertionNum(int registertionNum) {
		this.registertionNum = registertionNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public Card getPlayedCard() {
		return playedCard;
	}

	public void setPlayedCard(Card playedCard) {
		this.playedCard = playedCard;
	}

	public String getPlayerJson() {
		return playerJson;
	}

	public void setPlayerJson(JSONObject playerJson, int event) {
		playerJson.put("event", event);
		playerJson.put("state", this.state.id());
		playerJson.put("hand", this.getHandJson());
		//playerJson.put("playedcard", this.cardToJson(this.playedCard));
		this.playerJson = playerJson.toString();
	}
	
	public JSONArray getHandJson()
	{
		JSONArray cards = new JSONArray();
		
		for(int i = 0; i < hand.size(); i++)
		{
			cards.put(cardToJson(hand.get(i)));
		}
		
		return cards;
	}
	
	public JSONObject cardToJson(Card card)
	{
		JSONObject cardJson = new JSONObject();
		cardJson.put("id", card.getId());
		cardJson.put("message", card.getMessage());
		return cardJson;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void win()
	{
		this.score++;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Player)
		{
			return ((Player)o).getId() == this.getId();
		}
		else
			return false;
	}

	public boolean haveCard(Card card) 
	{
		return this.hand.contains(card);
	}

	public boolean isHasPlayed() {
		return hasPlayed;
	}

	public void setHasPlayed(boolean hasPlayed) {
		this.hasPlayed = hasPlayed;
	}
}
