package swen221.cardgame.cards.core;

import java.io.Serializable;

import static swen221.cardgame.cards.core.Card.Suit.CLUBS;
import static swen221.cardgame.cards.core.Card.Suit.DIAMONDS;
import static swen221.cardgame.cards.core.Card.Suit.SPADES;

public class Card implements Comparable<Card>, Serializable {

	/**
	 * Represents a card suit.
	 *
	 * @author David J. Pearce
	 */
	public enum Suit {
		HEARTS,
		CLUBS,
		DIAMONDS,
		SPADES;
	}

	/**
	 * Represents the different card "numbers".
	 *
	 * @author David J. Pearce
	 */
	public enum Rank {
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING,
		ACE;
	}

	// =======================================================
	// Card stuff
	// =======================================================

	private Suit suit; // HEARTS, CLUBS, DIAMONDS, SPADES
	private Rank rank; // 2 <= number <= 14 (ACE)

	/**
	 * Construct a card in the given suit, with a given number
	 *
	 * @param suit   --- between 0 (HEARTS) and 3 (SPADES)
	 * @param number --- between 2 and 14 (ACE)
	 */
	public Card(Suit suit, Rank number) {
		this.suit = suit;
		this.rank = number;
	}

	/**
	 * Get the suit of this card, between 0 (HEARTS) and 3 (SPADES).
	 *
	 * @return
	 */
	public Suit suit() {
		return suit;
	}

	/**
	 * Get the number of this card, between 2 and 14 (ACE).
	 *
	 * @return
	 */
	public Rank rank() {
		return rank;
	}

	private static String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
	private static String[] ranks = {"2 of ", "3 of ", "4 of ",
			"5 of ", "6 of ", "7 of ", "8 of ", "9 of ", "10 of ", "Jack of ",
			"Queen of ", "King of ", "Ace of "};

	public String toString() {
		return ranks[rank.ordinal()] + suits[suit.ordinal()];
	}

	@Override
	public int compareTo(Card o) {
		int me = 14 * this.suit().ordinal() + this.rank().ordinal();
		int other = 14 * o.suit().ordinal() + o.rank().ordinal();

		if (me > other) return 1;
		else if (me < other) return -1;
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;

		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		if (obj instanceof Card) {
			Card card = (Card) obj;
			if (rank.ordinal() != card.rank().ordinal() || suit().ordinal() != card.suit().ordinal()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}
}
