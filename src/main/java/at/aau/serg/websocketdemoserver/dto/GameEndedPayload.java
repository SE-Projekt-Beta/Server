package at.aau.serg.websocketdemoserver.dto;

import java.util.List;

public class GameEndedPayload {
    private List<PlayerRankingEntry> ranking;

    public GameEndedPayload() {}

    public GameEndedPayload(List<PlayerRankingEntry> ranking) {
        this.ranking = ranking;
    }

    public List<PlayerRankingEntry> getRanking() {
        return ranking;
    }

    public void setRanking(List<PlayerRankingEntry> ranking) {
        this.ranking = ranking;
    }
    public static class PlayerRankingEntry {
        private int rank;
        private String nickname;
        private int wealth;

        public PlayerRankingEntry() {}

        public PlayerRankingEntry(int rank, String nickname, int wealth) {
            this.rank = rank;
            this.nickname = nickname;
            this.wealth = wealth;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getWealth() {
            return wealth;
        }

        public void setWealth(int wealth) {
            this.wealth = wealth;
        }
    }
}
