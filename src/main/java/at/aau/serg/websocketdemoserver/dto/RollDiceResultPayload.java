package at.aau.serg.websocketdemoserver.dto;

public class RollDiceResultPayload {
    private MovePlayerPayload move;
    private CurrentPlayerPayload next;

    public MovePlayerPayload getMove() {
        return move;
    }

    public void setMove(MovePlayerPayload move) {
        this.move = move;
    }

    public CurrentPlayerPayload getNext() {
        return next;
    }

    public void setNext(CurrentPlayerPayload next) {
        this.next = next;
    }
}
