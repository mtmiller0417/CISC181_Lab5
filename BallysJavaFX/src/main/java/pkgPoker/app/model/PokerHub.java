package pkgPoker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import netgame.common.Hub;
import pkgPokerBLL.Action;
import pkgPokerBLL.Card;
import pkgPokerBLL.CardDraw;
import pkgPokerBLL.Deck;
import pkgPokerBLL.GamePlay;
import pkgPokerBLL.GamePlayPlayerHand;
import pkgPokerBLL.Player;
import pkgPokerBLL.Rule;
import pkgPokerBLL.Table;

import pkgPokerEnum.eAction;
import pkgPokerEnum.eCardDestination;
import pkgPokerEnum.eDrawCount;
import pkgPokerEnum.eGame;
import pkgPokerEnum.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action) message;
			switch (act.getAction()) {
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case Leave:			
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case StartGame: //this is the **** thing
				// Get the rule from the Action object.
				Rule rle = new Rule(act.geteGame());				
				//TODO Lab #5 - If neither player has 'the button', pick a random player
				//		and assign the button.	

				//TODO Lab #5 - Start the new instance of GamePlay
				
				 HubGamePlay = new GamePlay(rle, UUID.randomUUID());
				// Add Players to Game
				 HubGamePlay.setGamePlayers(HubPokerTable.getHmPlayer()); 			
				// Set the order of players
				 HubGamePlay.setiActOrder(HubGamePlay.GetOrder(((Action) message).getPlayer().getiPlayerPosition()));
				


			case Draw:
				//TODO Lab #5 -	Draw card(s) for each player in the game.
				//TODO Lab #5 -	Make sure to set the correct visibility
				//TODO Lab #5 -	Make sure to account for community cards
				iDealNbr += iDealNbr;
				eDrawCount c = eDrawCount.geteDrawCount(iDealNbr);
				HubGamePlay.seteDrawCountLast(c);
				//HubGamePlay.getRule().GetDrawCard(c);
				//HubGamePlay.getRule().GetDrawCard(c).getCardCount().getCardCount();
				boolean test = HubGamePlay.getRule().GetDrawCard(c).getCardDestination().equals(eCardDestination.Community);
				System.out.println(test);
				for(int i = 1; i < HubGamePlay.getRule().GetDrawCard(c).getCardCount().getCardCount(); i++)
				{
					if(test)					
					{
						Card card2 = HubGamePlay.getGameDeck().Draw();
						HubGamePlay.getGameCommonHand().AddCardToHand(card2);
						continue;
					}
					for(int j = 1; j <= HubPokerTable.getHmPlayer().size(); j++)
					{
						Card card = HubGamePlay.getGameDeck().Draw();
						HubGamePlay.getPlayersHands().get(HubGamePlay.getPlayerByPosition(j).getiPlayerPosition()).AddCardToHand(card);
					}
				}	

				
				

				//TODO Lab #5 -	Check to see if the game is over
				resetOutput();
				//	Send the state of the gameplay back to the clients
				sendToAll(HubGamePlay);
				boolean check = HubGamePlay.isGameOver();
				if(check)
				{
					//Don't break, let it fall to ScoreGame
				}
				else
				{
					break;
				}
				
			case ScoreGame:
				// Am I at the end of the game?

				resetOutput();
				sendToAll(HubGamePlay);
				break;
			}
			
		}

	}

}