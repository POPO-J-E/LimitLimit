package com.server.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import com.server.GameEventDispatcher;
import com.server.GameResponseDispatcher;
import com.server.GameServerMain;
import com.server.LoggerManager;
import com.server.db.DeckBuilder;

public class GameManager 
{
	private final static Logger LOG = LoggerManager.GetLogger(GameServerMain.class.getName());
	
	public static enum State {WAITING_FOR_PLAYERS, WAITING_FOR_PLAYS, WAITING_FOR_CHOOSE, WAITING_PAUSE};
	
	private static final int HAND_SIZE = 5;
	
	private volatile Map<Integer, Player> players;
	private final Deck deck;
	private final Deck deckBlack;
	private Player winner;
	private BlackCard blackCard;

	private final GameEventDispatcher eventDispatcher;
	
	private State state;
	private Random rand;
	
	private DeckBuilder deckBuilder;
	
	private int numberOfPlayers;
	private int numberOfPlays;
	
	public GameManager()
	{
		final ResourceBundle configurationBundle = ResourceBundle.getBundle("configuration");
		//hashmap with predictable iteration order
		players = new LinkedHashMap<Integer, Player>();
		eventDispatcher = new GameEventDispatcher(this);
		deck = new Deck();
		deckBlack = new Deck();
		state = State.WAITING_FOR_PLAYERS;
		numberOfPlayers = 0;
		numberOfPlays = 0;
		
		rand = new Random();
		
		deckBuilder = new DeckBuilder(configurationBundle.getString("db_url"), 
				configurationBundle.getString("db_name"), 
				configurationBundle.getString("db_user"), 
				configurationBundle.getString("db_password"));
	}

	public Map<Integer, Player> getPlayers() {
		return players;
	}

	public void setPlayers(Map<Integer, Player> players) {
		this.players = players;
	}

	public Deck getDeck() {
		return deck;
	}
	
	public int getPlayerIndexByKey(int playerKey)
	{
		int pos = new ArrayList<Integer>(players.keySet()).indexOf(playerKey);
		return pos;
	}
	
	public Player getPlayerByIndex(int inx)
	{
		List<Player> l = new ArrayList<Player>(players.values());
		Player p = l.get(inx);
		return p;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
		winner.setWinner(true);
	}

	public GameEventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public Player getPlayer(int playerId) 
	{
		if(players.containsKey(playerId))
			return players.get(playerId);
		return null;
	}
	
	public void populateHands()
	{
		Iterator<Entry<Integer, Player>> it = this.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        populateHand(playerIt);
	    }
	}
	
	public void initPlayers()
	{
		Iterator<Entry<Integer, Player>> it = this.getPlayers().entrySet().iterator();
	    while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        Player playerIt = ((Player)pair.getValue());
	        playerIt.setPlayedCard(null);
	        playerIt.setHasPlayed(false);
	        playerIt.setWinner(false);
	    }
	    
	    this.numberOfPlayers = players.size() - 1;
	    this.numberOfPlays = 0;
	}
	
	private void populateHand(Player player)
	{
		int handSize = player.getHand().size();
		
		for(int i = handSize; i < GameManager.HAND_SIZE; i++)
		{
			Card card = this.deck.drawCard();
			player.getHand().add(card);
			LOG.fine(player.getUserName() + " draw card " + "\""+card+"\"");
		}
	}

	public boolean canBegin() {
		return state == State.WAITING_FOR_PLAYERS && this.getPlayers().size() > 2;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void begin() {
		populateDecks();
		nextTurn(null);
	}
	
	public void nextTurn(Player winner) {
		this.deckBlack.discard(this.blackCard);
		this.blackCard = (BlackCard)this.deckBlack.drawCard();
		
		this.state = State.WAITING_FOR_PLAYS;

		this.initPlayers();
		
		if(winner == null)
		{
			chooseWinner();
		}
		else
		{
			this.setWinner(winner);
		}
		
		this.populateHands();
	}
	
	private void chooseWinner() 
	{
		this.setWinner(this.getPlayerByIndex(rand.nextInt(this.getPlayers().size())));
	}

	private void populateDecks()
	{
		deck.clear();
		deckBuilder.populateDeck(deck, true);
		deckBlack.clear();
		deckBuilder.populateDeck(deckBlack, false);
		
		//TODO
		this.deck.addCard("Abstinence.");
		this.deck.addCard("Goats eating coins.");
		this.deck.addCard("Inappropriate yelling.");
		this.deck.addCard("The plot of a Michael Bay movie.");
		this.deck.addCard("The Red Menace.");
		this.deck.addCard("Vehicular homicide.");
		this.deck.addCard("A caress of the inner thigh");
		this.deck.addCard("Filling Sean Hannity with helium and watching him float away.");
		this.deck.addCard("The homosexual lifestyle.");
		this.deck.addCard("Swag.");
		this.deck.addCard("A ball of earwax, semen, and toenail clippings.");
		this.deck.addCard("A Fleshlight.");
		this.deck.addCard("A man on the brink of orgasm.");
		this.deck.addCard("A saxophone solo.");
		this.deck.addCard("An endless stream of diarrhea.");
		this.deck.addCard("Bio-engineered assault turtles with acid breath.");
		this.deck.addCard("Chunks of dead hitchhiker.");
		this.deck.addCard("Court-ordered rehab.");
		this.deck.addCard("Crumbs all over the god damn carpet.");
		this.deck.addCard("Daniel Radcliffe’s delicious asshole.");
		this.deck.addCard("Dark and mysterious forces beyond our control.");
		this.deck.addCard("Giving birth to the Antichrist.");
		this.deck.addCard("Having anuses for eyes.");
		
		this.deck.shuffle();
		/*Hip hop jewels
		Holding down a child and farting all over him.
		Inserting a Mason jar into my anus.
		Invading Poland.
		Jobs.
		Joe Biden.
		Magnets.
		Miley Cyrus at 55.
		My black ass.
		Our first chimpanzee President.
		Penis breath.
		Running out of semen.
		Saying “I love you.”
		Seeing Grandma naked.
		Sex with Patrick Stewart.
		Sexual peeing.
		The cool, refreshing taste of Pepsi®.
		The morbidly obese.
		The Patriarchy.
		The pirate’s life.
		The rhythms of Africa.
		The wonders of the Orient.
		The wrath of Vladimir Putin.
		This year’s mass shooting.
		Three dicks at the same time.
		White-man scalps.
		Your weird brother.*/
		
		this.deckBlack.addBlackCard("Sorry everyone, I just ______.");
		this.deckBlack.addBlackCard("Sorry everyone, I just ______.");
		this.deckBlack.addBlackCard("What’s the next superhero?");
		this.deckBlack.addBlackCard("When I’m in prison, I’ll have ______ smuggled in.");
		this.deckBlack.addBlackCard("Who stole the cookies from the cookie jar?");
		this.deckBlack.addBlackCard("A recent laboratory study shows that undergraduates have 50% less sex after being exposed to ______.");
		this.deckBlack.addBlackCard("Fun tip! When your man asks you to go down on him, try surprising him with ______ instead.");
		this.deckBlack.addBlackCard("I get by with a little help from ______.");
		this.deckBlack.addBlackCard("Introducing the amazing superhero/sidekick duo! It’s ______ and ______!");
		this.deckBlack.addBlackCard("Introducing X-Treme Baseball! It’s like baseball, but with ______!");
		this.deckBlack.addBlackCard("The new Chevy Tahoe. With the power and space to take ______ everywhere you go.");
		
		this.deckBlack.shuffle();
		/*They said we were crazy. They said we couldn’t put ______ inside of ______. They were wrong.
		Today on Maury: “Help! My son is ______!”
		What is George W. Bush thinking about right now?*/
		
	}

	public BlackCard getBlackCard() {
		return blackCard;
	}

	public void setBlackCard(BlackCard blackCard) {
		this.blackCard = blackCard;
	}

	public boolean allPlaysDone() 
	{
		return numberOfPlays >= numberOfPlayers;
	}

	public void onPlayDone() {
		this.numberOfPlays++;
	}
}
