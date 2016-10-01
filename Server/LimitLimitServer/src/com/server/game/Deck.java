package com.server.game;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.server.GameServerMain;
import com.server.LoggerManager;

public class Deck 
{
	private final static Logger LOG = LoggerManager.GetLogger(GameServerMain.class.getName());
		
	private List<Card> cards;
	private List<Card> cardsInDeck;
	private List<Card> discardedCards;
	
	public Deck(List<Card> cards)
	{
		this.cards = cards;
		this.cardsInDeck = new LinkedList<Card>(cards);
		this.discardedCards = new LinkedList<Card>(cards);
	}
	
	public Deck()
	{
		clear();
	}
	
	public void clear()
	{
		this.cards = new LinkedList<Card>();
		this.cardsInDeck = new LinkedList<Card>();
		this.discardedCards = new LinkedList<Card>();
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public List<Card> getCardsInDeck() {
		return cardsInDeck;
	}

	public void setCardsInDeck(List<Card> cards_in_deck) {
		this.cardsInDeck = cards_in_deck;
	}
	
	public Card drawCard()
	{
		Card card = cardsInDeck.get(0);
		discardedCards.add(card);
		cardsInDeck.remove(0);
		
		if(cardsInDeck.size() == 0)
		{
			populateAndShuffle();
		}
		return card;
	}
	
	public void discard(Card card)
	{
		discardedCards.add(card);
	}
	
	private void populateAndShuffle()
	{
		cardsInDeck = new LinkedList<Card>(discardedCards);
		discardedCards = new LinkedList<Card>();
		
		shuffle();
	}
	
	public void shuffle()
	{
		int nb_cards = cards.size();
		
		for(int i=0; i<nb_cards; i++) {
	        int card = (int) (Math.random() * (nb_cards-i));
	        cardsInDeck.add(cardsInDeck.remove(card));
	    }
	}
	
	private int cardIds = 0;
	
	public void addCard(String message)
	{
		addCard(new Card(cardIds++, message));
	}

	public void addBlackCard(String message) 
	{
		addCard(new BlackCard(cardIds++, message));
	}
	
	public void addCard(Card card)
	{
		cards.add(card);
		cardsInDeck.add(card);
	}
}
