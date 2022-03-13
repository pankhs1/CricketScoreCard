package com.company.Model;

import com.company.Enums.BallType;
import com.company.Enums.InningStatus;
import com.company.Enums.RunType;
import com.company.Enums.WicketType;
import com.company.Exceptions.InvalidMatch;
;
import com.company.Repository.MatchRepository;
import com.company.Repository.ScoreCardRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ScoreCard {
    private Map<Player, PlayerScore> playerScores = new HashMap<>();
    private Map<Player, BowlerOver> bowlerOvers = new HashMap<>();
    private int totalExtras;
    private int totalScore;
    private int totalWickets;
    private int totalByes;
    private int totalLegByes;
    private String match;
    private int innings;

    private ScoreCard(String match, int innings) throws InvalidMatch {
        if (MatchRepository.matchMap.get(match) == null)
            throw new InvalidMatch("No match exists");
        this.match = match;
        this.innings = innings;

    }

    public static ScoreCard INSTANCE(String matchName, int inningsNumber) throws InvalidMatch {
        if (MatchRepository.matchMap.get(matchName) == null)
            throw new InvalidMatch("No match exists");
        Match match = MatchRepository.matchMap.get(matchName);
        if (ScoreCardRepository.scoreCardMap.get(matchName) == null) {
            Innings innings = match.getInnings().get(inningsNumber);
            ScoreCard scoreCard = new ScoreCard(matchName, inningsNumber);
            HashMap<Integer, ScoreCard> scoreCardMap = new HashMap<>();
            scoreCardMap.putIfAbsent(inningsNumber, scoreCard);
            ScoreCardRepository.scoreCardMap.putIfAbsent(matchName, scoreCardMap);
        }
        if(ScoreCardRepository.scoreCardMap.get(matchName).get(inningsNumber)==null)
        {
            ScoreCard scoreCard = new ScoreCard(matchName, inningsNumber);
            Map<Integer, ScoreCard> scoreCardMap = ScoreCardRepository.scoreCardMap.get(matchName);
            scoreCardMap.putIfAbsent(inningsNumber, scoreCard);
            ScoreCardRepository.scoreCardMap.putIfAbsent(matchName, scoreCardMap);
        }
        return ScoreCardRepository.scoreCardMap.get(matchName).get(inningsNumber);
    }


    public void setScoreCardForBall(Ball ball) {
        Match match = MatchRepository.matchMap.get(this.match);
        Innings innings = match.getInnings().get(this.innings);
        if( innings.getInning_status()== InningStatus.IN_PROGRESS) {
            addOver(ball.getOverNumber(), ball.getBowledBy());
            innings.getOvers().get(ball.getOverNumber());
            Over over;
            switch (ball.getBallType()) {
                case WIDE:
                case BYES:
                    updateExtras(ball);
                    break;
                case NORMAL: {
                    getOrCreatePlayerScore(ball);
                    updateScore(ball, innings);
                    BowlerOver bowlerOver = setBowlerOverForBall(ball);
                    over = bowlerOver.getOverMap().get(ball.getOverNumber());
                    over.getBalls().add(ball);
                    break;
                }
                case WICKET: {
                    PlayerScore playerScore = getOrCreatePlayerScore(ball);
                    updateScore(ball, innings);
                    playerScore.setOut(true);

                    totalWickets += 1;
                    playerScore.setWicketType(ball.getWicket().getWicketType());
                    playerScore.setBowler(ball.getBowledBy());
                    BowlerOver bowlerOver = setBowlerOverForBall(ball);
                    incrementWicketForBall(ball.getBowledBy(), ball.getBallType(),
                            ball.getWicket().getWicketType());
                    over = bowlerOver.getOverMap().get(ball.getOverNumber());
                    over.getBalls().add(ball);
//                    System.out.println(innings.getPlayerNumber() + " " + innings.getPlayers().size());
                    if(ScoreCardRepository.scoreCardMap.get("1").get(innings.getInningsNumber()).getTotalWickets()<innings.getPlayers().size()-1)
                    {
                        innings.setPlayerNumber(innings.getPlayerNumber()+1);
                        innings.setStrikePlayer(innings.getPlayers().get(totalWickets+1));
                    }
                    break;
                }
                case NO_BALL: {
                    updateExtras(ball);
                    break;
                }
            }
        }
        else
        {
            System.out.println("Innings Already Completed");
        }
    }



    private void addOver(int overNumber ,Player bowlerName) {
        Match match = MatchRepository.matchMap.get(this.match);
        Innings innings = match.getInnings().get(this.innings);
        innings.getOvers().putIfAbsent(overNumber, new Over(overNumber,bowlerName));
    }

    private PlayerScore getOrCreatePlayerScore(Ball ball) {
        PlayerScore playerScore = this.playerScores.get(ball.getFacedBy());
        if (playerScore == null) {
            playerScore = new PlayerScore(ball.getFacedBy());
            playerScores.put(ball.getFacedBy(), playerScore);
        }
        return playerScore;
    }

    private BowlerOver setBowlerOverForBall(Ball ball) {
        BowlerOver bowlerOver = this.bowlerOvers.get(ball.getBowledBy());
        if (bowlerOver == null) {
            bowlerOver = new BowlerOver(ball.getBowledBy());
            bowlerOvers.put(ball.getBowledBy(), bowlerOver);
        }
        bowlerOver.getOverMap().putIfAbsent(ball.getOverNumber(), new Over(ball.getOverNumber(), ball.getBowledBy()));
        return bowlerOver;
    }

    private void updateScore(Ball ball, Innings innings) {
        PlayerScore playerScore = playerScores.get(ball.getFacedBy());
        playerScore.setBowlsPlayed(playerScore.getBowlsPlayed()+1);
        switch (ball.getRunType()) {
            case ONE:
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 1);
                incrementScore(1);
                changeStrike(innings);
                incrementRunsScoredInOver(ball.getOverNumber(),1);
                break;
            case TWO:
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 2);
                incrementScore(2);
                incrementRunsScoredInOver(ball.getOverNumber(),2);

                break;
            case THREE:
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 3);
                incrementScore(3);
                changeStrike(innings);
                incrementRunsScoredInOver(ball.getOverNumber(),3);

                break;
            case FOUR:
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 4);
                incrementScore(4);
                incrementRunsScoredInOver(ball.getOverNumber(),4);

                break;
            case SIX:
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 6);
                incrementScore(6);
                incrementRunsScoredInOver(ball.getOverNumber(),6);
                break;
        }
    }

    public static void changeStrike(Innings innings) {
        Player strikePlayer = innings.getStrikePlayer();
        innings.setStrikePlayer(innings.getNonStrikePlayer());
        innings.setNonStrikePlayer(strikePlayer);
    }

    private void updateExtras(Ball ball) {
        switch (ball.getRunType()) {
            case ONE_BYE:
            case ONE_LEG_BYE:
                incrementScore(1);
                incrementExtras(1);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                break;
            case TWO_BYE:
            case TWO_LEG_BYES:
                incrementScore(2);
                incrementExtras(2);
                incrementExtrasInOver(ball.getOverNumber(), 2);
                break;
            case THREE_BYE:
            case THREE_LEG_BYES:
                incrementScore(3);
                incrementExtras(3);
                incrementExtrasInOver(ball.getOverNumber(), 3);
                break;
            case FOUR_BYE:
            case FOUR_LEG_BYES:
                incrementScore(4);
                incrementExtras(4);
                incrementExtrasInOver(ball.getOverNumber(), 4);
                break;
            case ONE_WIDE:
            case ZERO_NO_BALL:
                incrementScore(1);
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementExtras(1);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                break;
            case TWO_WIDES:
                incrementScore(2);
                incrementExtras(2);
                incrementExtrasInOver(ball.getOverNumber(), 2);
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                break;
            case THREE_WIDES:
                incrementScore(3);
                incrementExtras(3);
                incrementExtrasInOver(ball.getOverNumber(), 3);
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                break;
            case FOUR_WIDES:
                incrementScore(4);
                incrementExtras(4);
                incrementExtrasInOver(ball.getOverNumber(), 4);
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                break;
            case FIVE_WIDES:
                incrementScore(5);
                incrementExtras(5);
                incrementExtrasInOver(ball.getOverNumber(), 5);
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                break;
            case ONE_NO_BALL:
                incrementScore(2);
                incrementExtras(1); // one hit + 1 nb
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 1);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                incrementRunsScoredInOver(ball.getOverNumber(), 1);
                break;
            case TWO_NO_BALLS:
                incrementScore(3);
                incrementExtras(1); // one hit + 1 nb
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 2);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                incrementRunsScoredInOver(ball.getOverNumber(), 2);
                break;
            case THREE_NO_BALLS:
                incrementScore(4);
                incrementExtras(1); // one hit + 1 nb
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 3);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                incrementRunsScoredInOver(ball.getOverNumber(), 3);
                break;
            case FOUR_NO_BALLS:
                incrementScore(5);
                incrementExtras(1); // one hit + 1 nb
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 4);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                incrementRunsScoredInOver(ball.getOverNumber(), 4);
                break;
            case SIX_NO_BALLS:
                incrementScore(7);
                incrementExtras(1); // one hit + 1 nb
                incrementBowlerExtras(ball.getBowledBy(),
                        ball.getBallType(), 1);
                incrementPlayerScore(ball.getFacedBy(), ball.getRunType(), 6);
                incrementExtrasInOver(ball.getOverNumber(), 1);
                incrementRunsScoredInOver(ball.getOverNumber(), 6);
                break;
        }
    }

    private void incrementBowlerExtras(Player bowledBy, BallType ballType, int extras) {
        if (bowlerOvers.get(bowledBy) == null) {
            bowlerOvers.putIfAbsent(bowledBy, new BowlerOver(bowledBy));
        }
        if (bowlerOvers.get(bowledBy) != null && bowlerOvers.get(bowledBy).
                getExtrasBowled().get(ballType)
                != null) {

            int extra = bowlerOvers.get(bowledBy).getExtrasBowled().get(ballType);
            bowlerOvers.get(bowledBy).getExtrasBowled()
                    .put(ballType, extra + extras);
        } else

            bowlerOvers.get(bowledBy).getExtrasBowled()
                    .putIfAbsent(ballType, extras);
    }

    private void incrementWicketForBall(Player bowledBy, BallType ballType,
                                        WicketType wicketType) {
        if (bowlerOvers.get(bowledBy) == null) {
            bowlerOvers.putIfAbsent(bowledBy, new BowlerOver(bowledBy));
        }
        switch (wicketType) {
            case LBW:
            case CAUGHT:
            case BOWLED:
            case HIT_WICKET:
            case STUMPTED:
                bowlerOvers.get(bowledBy).setWicketsTaken(bowlerOvers.get(bowledBy)
                        .getWicketsTaken() + 1);
        }
    }

    private void incrementPlayerScore(Player player, RunType runType, int score) {
        PlayerScore playerScore = playerScores.get(player);
        playerScore.getRunsScored()
                .add(score);
        playerScore.setRuns(playerScore.getRuns()+score);
        playerScore.setStrikeRate(Double.valueOf(Double.valueOf(playerScore.getRuns())/ Double.valueOf(playerScore.getBowlsPlayed()))*100);
        switch (runType) {
            case FOUR:
            case FOUR_NO_BALLS:
                playerScore.setTotalBoundaries(playerScore.getTotalBoundaries() + 1);
                break;
            case SIX:
            case SIX_NO_BALLS:
                playerScore.setTotalSixes(playerScore.getTotalSixes() + 1);
                break;
        }
    }

    private void incrementScore(int incrementBy) {
        totalScore += incrementBy;
    }

    private void incrementExtras(int incrementBy) {
        totalExtras += incrementBy;
    }

    private void incrementRunsScoredInOver(int overNumber, int runs) {
        Match match = MatchRepository.matchMap.get(this.match);
        Innings innings = match.getInnings().get(this.innings);
        Over over = innings.getOvers().get(overNumber);
        over.setRunsScored(over.getRunsScored() + runs);
        over.setTotalRunsInOver(over.getRunsScored() + over.getExtras());
    }

    private void incrementExtrasInOver(int overNumber, int extras) {
        Match match = MatchRepository.matchMap.get(this.match);
        Innings innings = match.getInnings().get(this.innings);
        Over over = innings.getOvers().get(overNumber);
        over.setExtras(over.getExtras() + extras);
        over.setTotalRunsInOver(over.getRunsScored() + over.getExtras());
    }
}
