package swen221.cardgame.cards.util;

import swen221.cardgame.cards.core.Card;
import swen221.cardgame.cards.core.Player;
import swen221.cardgame.cards.core.Trick;

import java.util.*;

/**
 * Implements a simple computer player who plays the highest card available when
 * the trick can still be won, otherwise discards the lowest card available. In
 * the special case that the player must win the trick (i.e. this is the last
 * card in the trick), then the player conservatively plays the least card
 * needed to win.
 *
 * @author David J. Pearce
 *
 */
public class SimpleComputerPlayer extends AbstractComputerPlayer {

	public SimpleComputerPlayer(Player player) {
		super(player);
	}
	private Card bestCardToPlay = null; // Card that will be returned to play

	/**
	 * Based on multiple factors, getNextCard returns the next card
	 * that should be placed in the trick
	 * @param trick
	 * @return Card returns the next card to be played by the AI
	 */
	@Override
	public Card getNextCard(Trick trick) {
		bestCardToPlay = null;

		// If AI is the start player
		if (trick.getLeadPlayer() == this.player.direction) {
			aiLeadDecision(trick);
		}
		else {
			aiMakeDecision(trick);
		}
		return bestCardToPlay;
	}


	/**
	 * Method is called if the AI is the lead player and places the
	 * first card. The decision making at this stage is different
	 * to when the AI is the second/third/fourth player to put a
	 * card down.
	 *
	 * @param trick
	 */
	public void aiLeadDecision(Trick trick) {
		Set<Card> matchTrump = new HashSet<>(this.player.getHand().matches(trick.getTrumps()));

		// If AI has a card that matches trump suit, determine the highest trump card to play
		if (matchTrump.size() != 0) {
			for (Card card : matchTrump) {
				if (bestCardToPlay != null) {
					if (card.compareTo(bestCardToPlay) > 0) {
						bestCardToPlay = card;
					}
				}
				else {
					bestCardToPlay = card;
				}
			}
		}
		// If AI doesn't have a card that matches the trump suit (or trumps == null)
		else {
			// Find the highest card
			for (Card card : player.getHand()) {
				if (bestCardToPlay != null) {
					if (card.rank().ordinal() >= bestCardToPlay.rank().ordinal()) {
						// If two cards have the same rank value, choose the one with the better suit value
						if (card.rank().ordinal() == bestCardToPlay.rank().ordinal()) {
							if (card.suit().ordinal() > bestCardToPlay.suit().ordinal()) {
								bestCardToPlay = card;
							}
						}
						// Play a high rank card of a low suit (more likely to win)
						else {
							bestCardToPlay = card;
						}
					}
				}
				else {
					bestCardToPlay = card;
				}
			}
		}
	}

	/**
	 * This is called if the AI is not the lead player.
	 * This method sets the correct card to be placed
	 * based on factors like following/not following
	 * suits
	 * @param trick
	 */
	private void aiMakeDecision(Trick trick) {
		Set<Card> matchesLead = new TreeSet<>(this.player.getHand().matches(trick.getCardPlayed(trick.getLeadPlayer()).suit()));

		// If AI has a card of the same suit as the lead, we must follow suit
		if (matchesLead.size() != 0) {
			followSuit(trick, matchesLead);
		}
		else {
			dontFollowSuit(trick);
		}
	}

	private void followSuit(Trick trick, Set<Card> matchesLead) {
		Card bestCardPlayed = trick.getCardPlayed(trick.getLeadPlayer());
		for (Card card : trick.getCardsPlayed()) {
			// If the card played is of the type played by the lead
			if (card.suit().ordinal() == trick.getCardPlayed(trick.getLeadPlayer()).suit().ordinal()) {
				// If this card is the same suit as the lead and is higher than the current highest played card
				// with that suit
				if (card.rank().ordinal() > bestCardPlayed.rank().ordinal()) {
					bestCardPlayed = card;
				}
			}
		}

		// Checking to see whether the best card in AI's hand is better than the best card that's been played
		// if so, play it. If it's not better than that card, discard the lowest card of that suit in AI's hand
		Card bestCard = bestCardPlayed;
		Card worstCard = bestCardPlayed;
		for (Card card : matchesLead) {
			// If we find a card with a higher value, then play this card
			// (unless theres another card in the rest of the hand has a higher ordinal value card for that suit)
			if (card.rank().ordinal() > bestCard.rank().ordinal()) {
				bestCard = card;

				// This is the special case, if we're the last to play and we can win
				// (which we can because we're here), we should play the lowest card that will give us a win.
				// as 'matchesLead' is sorted by card order, we can break because subsequent cards will be greater
				if (trick.getCardsPlayed().size() == 3) {
					break;
				}
			}
			if (card.rank().ordinal() < worstCard.rank().ordinal()) {
				worstCard = card;
			}
		}

		// Return either the best card in that suit if it's better than the highest card in the trick
		// Otherwise return the worst card in that suit because there wasn't a card in the AI's suit better than
		// the highest card in the trick
		if (!bestCard.equals(bestCardPlayed)) {
			bestCardToPlay = bestCard;
		}
		else {
			bestCardToPlay = worstCard;
		}
	}

	// Called when AI's hand didn't have a suit match with lead player card suit.
	// I.E Can't follow suit
	private void dontFollowSuit(Trick trick) {
		Set<Card> matchesTrump = new TreeSet<>(this.player.getHand().matches(trick.getTrumps()));

		// AI has a card that can be used to trump
		if (matchesTrump.size() != 0) {
			// Determines whether there's a trump card in the played trick. If there is, highestTrumpCard wont be null
			// It will equal the highest trump card in the trick.
			Card highestTrumpCardInTrick = getHighestTrumpCardInTrick(trick);
			if (highestTrumpCardInTrick != null) {
				// Checks if there is a trump card in the trick already that is higher than the AI's highest trump card,
				// if so, we can prematurely return the lowest card as we don't want to play our trump when we won't win
				if (highestTrumpCardInTrick.rank().ordinal() > getHighestTrumpCardInHand(trick).rank().ordinal()) {
					bestCardToPlay = getLowestCardInHand();
					return;
				}
			}

			// We know that if there's a trump card in the trick, it must be lower than AI hand's max trump card
			if (trick.getCardsPlayed().size() < 3) {
				bestCardToPlay = getHighestTrumpCardInHand(trick);
			}
			// The AI is the last player to place, We can win. We just need to find the lowest card above the highest
			// trump card in the trick
			else {
				for (Card card : matchesTrump) {
					if (highestTrumpCardInTrick != null) {
						if (card.rank().ordinal() > highestTrumpCardInTrick.rank().ordinal()) {
							bestCardToPlay = card;
							break;
						}
					}
					else {
						bestCardToPlay = card;
						break;
					}
				}
			}
		}
		else {
			// No card can trump, all cards eligible. Need to find the lowest card to discard
			bestCardToPlay = getLowestCardInHand();
		}
	}

	// Helper Methods

	/**
	 * Returns the lowest card in the AI's hand
	 * @return Card
	 */
	private Card getLowestCardInHand() {
		Card lowestCard = null;
		for (Card card : this.player.getHand()) {
			if (lowestCard == null) lowestCard = card;
			else {
				if (card.compareTo(lowestCard) < 0) {
					lowestCard = card;
				}
			}
		}
		return lowestCard;
	}

	/**
	 * It returns the highest trump card that has been placed.
	 * If a trump card hasn't been placed, it returns null
	 * @param trick
	 * @return Card
	 */
	private Card getHighestTrumpCardInTrick(Trick trick) {
		Card highestTrumpCard = null;
		for (Card card : trick.getCardsPlayed()) {
			if (card.suit().ordinal() == trick.getTrumps().ordinal()) {
				if (highestTrumpCard == null) highestTrumpCard = card;
				else {
					if (card.rank().ordinal() > highestTrumpCard.rank().ordinal()) {
						highestTrumpCard = card;
					}
				}
			}
		}
		return highestTrumpCard;
	}

	/**
	 * It returns the highest trump card in the AI's hand
	 * If the AI doesn't have a trump card, it returns null
	 * @param trick
	 * @return
	 */
	private Card getHighestTrumpCardInHand(Trick trick) {
		Card highestTrumpCard = null;
		for (Card card : this.player.getHand()) {
			if (card.suit().ordinal() == trick.getTrumps().ordinal()) {
				if (highestTrumpCard == null) highestTrumpCard = card;
				else {
					if (card.rank().ordinal() > highestTrumpCard.rank().ordinal()) {
						highestTrumpCard = card;
					}
				}
			}
		}
		return highestTrumpCard;
	}
}
