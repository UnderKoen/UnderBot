package nl.underkoen.underbot.hitbox;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface WinnerRaffle extends Response {
    default String getMethod() {return "winnerRaffle";}

    String getChannel();

    UserInfo getWinner();

    String getWinnerEmail();

    Boolean isWinnerPicked();

    Boolean isForAdmin();

    String getAnswer();
}
