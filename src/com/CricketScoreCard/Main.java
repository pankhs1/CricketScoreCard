package com.CricketScoreCard;

import com.CricketScoreCard.Enums.*;
import com.CricketScoreCard.Exceptions.InvalidMatch;
import com.CricketScoreCard.Model.*;
import com.CricketScoreCard.Repository.PlayerRepository;
import com.CricketScoreCard.Repository.ScoreCardRepository;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

	private static ScoreService ScoreSerivce;

	public static void main(String[] args) throws InvalidMatch {
	 Team ind = new Team("India");
	 Team aus = new Team("Australia");
	 PlayingMembers indplaying = new PlayingMembers();
	 PlayingMembers ausPlaying = new PlayingMembers();
	 indplaying.setPlayers(Arrays.asList(new Player("Dhawan"), new Player("Rohit"),new Player("Kohli"),new Player("Pant"),new Player("Jadeja")));
	 ausPlaying.setPlayers(Arrays.asList(new Player("Finch"), new Player("Warner"),new Player("Smith"),new Player("Maxwell"),new Player("Stoinis")));
	 Match match = new Match(new TeamsBetween(ind,aus,indplaying,ausPlaying),2);
	 Toss toss = new Toss();
	 toss.setTossedBy("Aaron Finch");
	 toss.setAskedBy("Rohit Sharma");
	 match.setMatchId("1");
	 match.setToss(toss);


	 toss.setWonByTeam(ind.getName());
	 toss.setTossAction(TossAction.BATTING);
	 match.setToss(toss);
	 match.setMatchResult(MatchResult.ONGOING);
	 Admin admin = new Admin("admin1");
	 admin.addMatch(match);
	 startMatch(match);
	 Innings innings1 = new Innings(1,ind);
	 innings1.setPlayers(indplaying.getPlayers());

	 match.setInnings(new HashMap<Integer,Innings>(){{put(1,innings1);}});
	 innings1.setPlayerNumber(0);
	 innings1.setStrikePlayer(innings1.getPlayers().get(innings1.getPlayerNumber()));
	 innings1.setPlayerNumber(1);
	 innings1.setNonStrikePlayer(innings1.getPlayers().get(innings1.getPlayerNumber()));
	 innings1.setInning_status(InningStatus.IN_PROGRESS);
	 startInnings(match,innings1);
	 Innings innings2 = new Innings(2,aus);
	 innings2.setPlayers(ausPlaying.getPlayers());
	 match.getInnings().put(2,innings2);
	 innings2.setPlayerNumber(1);
	 innings2.setStrikePlayer(innings2.getPlayers().get(innings2.getPlayerNumber()));
	 innings2.setPlayerNumber(2);
	 innings2.setNonStrikePlayer(innings2.getPlayers().get(innings2.getPlayerNumber()));
	 innings2.setInning_status(InningStatus.IN_PROGRESS);
	 startInnings(match,innings2);
	 if(ScoreCardRepository.scoreCardMap.get("1").get(1).getTotalScore()>ScoreCardRepository.scoreCardMap.get("1").get(2).getTotalScore())
		{
			System.out.println(innings1.getBattingTeam().getName() + " Won By " + (ScoreCardRepository.scoreCardMap.get("1").get(1).getTotalScore()-ScoreCardRepository.scoreCardMap.get("1").get(2).getTotalScore()) +  " runs" );
			System.out.println("-----------------------------------------------");
		}
	 else if(ScoreCardRepository.scoreCardMap.get("1").get(1).getTotalScore()==ScoreCardRepository.scoreCardMap.get("1").get(2).getTotalScore())
		{
			System.out.println("Match Drawn");
			System.out.println("-----------------------------------------------");
		}
	}

	private static void startInnings(Match match, Innings innings) throws InvalidMatch {
		innings.setInning_status(InningStatus.IN_PROGRESS);
		startBatting(match,innings);
	}
	public static void startMatch(Match match) throws InvalidMatch {
		match.setMatchResult(MatchResult.ONGOING);
	}

	private static void startBatting(Match match, Innings innings) throws InvalidMatch {
		System.out.println("Batting team is : " + innings.getBattingTeam().getName() + " playing for overs : "
				+ match.getNumberOfOvers());
		System.out.println("Playing Players");
		for(Player player : innings.getPlayers())
		{
			System.out.println(player.getName());
		}
		System.out.println("\n");
		Scanner scanner = new Scanner(System.in);
//		this.currentBattingTeam.initializePlayers();
		for (int i = 1; i <= match.getNumberOfOvers(); i++) {
			System.out.println("Enter Bowler Name");
			String bowlerName = scanner.nextLine();
			Over over = new Over(i,new Player(bowlerName));
			System.out.println("Currently playing over : " + i);
			while (over.getBalls().stream().filter(item->item.getBallType().equals(BallType.NORMAL)).collect(Collectors.toList()).size() + over.getBalls().stream().filter(item->item.getBallType().equals(BallType.WICKET)).collect(Collectors.toList()).size()!=6) {

				System.out.println("Enter Run");
				RunType runType = RunType.valueOf(scanner.nextLine().trim().toUpperCase(Locale.ROOT));
				System.out.println("Enter Ball Type");
				BallType ballType = BallType.valueOf(scanner.nextLine().trim().toUpperCase(Locale.ROOT));
				Ball ball = new Ball(1, over.getBowlerName(), innings.getStrikePlayer());
				ball.setRunType(runType);
				ball.setBallType(ballType);
				if(ball.getBallType()==BallType.WICKET)
				{
					ball.setWicket(new Wicket(WicketType.BOWLED,
							PlayerRepository.playerMap.get(innings.getStrikePlayer().toString()),
							PlayerRepository.playerMap.get(over.getBowlerName()),
							null, null, null));
				}

				over.getBalls().add(ball);
				ScoreSerivce.setScore(ball, match.getMatchId(), innings.getInningsNumber());
				if(ScoreCardRepository.scoreCardMap.get("1").get(innings.getInningsNumber()).getTotalWickets()>=innings.getPlayers().size()-1)
				{
					innings.setInning_status(InningStatus.COMPLETED);
					System.out.println(innings.getBattingTeam().getName() + " All Out ");
					displayScoreCard(ScoreCardRepository.scoreCardMap.get("1").get(innings.getInningsNumber()),innings);
					return;
				}
				if ( (innings.getInningsNumber() == 2
						&& ScoreCardRepository.scoreCardMap.get("1").get(2).getTotalScore() > ScoreCardRepository.scoreCardMap.get("1").get(1).getTotalScore())){
					displayScoreCard(ScoreCardRepository.scoreCardMap.get("1").get(innings.getInningsNumber()),innings);
					System.out.println(innings.getBattingTeam().getName() + " Won By " + (innings.getPlayers().size() - innings.getPlayerNumber()+1) + " wickets " );
					return;
				}
			}
			//end of over
			ScoreCard.changeStrike(innings);
			displayScoreCard(ScoreCardRepository.scoreCardMap.get("1").get(innings.getInningsNumber()),innings);
		}
	}

//	private static void displayMatchScoreCard(Map<Integer, ScoreCard> integerScoreCardMap) {
//		for(ScoreCard card : integerScoreCardMap.values())
//		{
//			displayScoreCard(card,ScoreCardRepository.scoreCardMap.get("1").get(card.getInnings()));
//		}
//	}

	private static void displayScoreCard(ScoreCard scoreCard,Innings innings) {
			System.out.println("\n");
		System.out.println("-----------------------------------------------");
			System.out.println( "Total Score " + scoreCard.getTotalScore());
			System.out.println("Total Byes " +scoreCard.getTotalByes());
			System.out.println("Wickets " + scoreCard.getTotalWickets());
			System.out.println("Extras " + scoreCard.getTotalExtras());
			System.out.println("Leg Byes " + scoreCard.getTotalLegByes());
		System.out.println("-----------------------------------------------");

			System.out.println("Name           Runs  four  sixes  balls strike Rate ");
			for(Player batsman : innings.getPlayers())
			{
				if(scoreCard.getPlayerScores().containsKey(batsman))
				{
					System.out.println(scoreCard.getPlayerScores().get(batsman).getName().getName() + (scoreCard.getPlayerScores().get(batsman).isOut()?" b " + scoreCard.getPlayerScores().get(batsman).getBowler().getName():" Not Out ") +  "    " + scoreCard.getPlayerScores().get(batsman).getRuns() + "   " +scoreCard.getPlayerScores().get(batsman).getTotalBoundaries()+ "    "+ scoreCard.getPlayerScores().get(batsman).getTotalSixes()+ "    "+ scoreCard.getPlayerScores().get(batsman).getBowlsPlayed() + "    " + scoreCard.getPlayerScores().get(batsman).getStrikeRate());
				}
				else
				{
					System.out.println(batsman.getName() + " Not Out " +  "  0  " +   "  0  "+  "  0  "+ "  0  " + " 0 ");
				}
				System.out.println("\n");
				System.out.println("-----------------------------------------------");
			}
			System.out.println("Name    Overs  Wickets ");
			for(Player bowler : scoreCard.getBowlerOvers().keySet())
			{
				System.out.println(scoreCard.getBowlerOvers().get(bowler).getBowlerName().getName() + "    " + scoreCard.getBowlerOvers().get(bowler).getOverMap().size() + "   " + scoreCard.getBowlerOvers().get(bowler).getWicketsTaken());
			}
		System.out.println("\n");
		System.out.println("-----------------------------------------------");
		}

}
