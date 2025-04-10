package at.aau.serg.websocketdemoserver.model.tiles;

import at.aau.serg.websocketdemoserver.model.Tile;

public class Event extends Tile {
        // What does this do?

        public Event(int position, String name) {
            super(position, name);
        }

        @Override
        public String getTileType(){
            String name = getName().toLowerCase();
            if (name.contains("risiko")) {
                return "event_risiko";
            } else if (name.contains("bank")){
                return "event_bank";
            } else {
                return "event";
            }
        }

}
