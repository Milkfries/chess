package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage{
    private GameData game;
    public LoadGameMessage(GameData game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGameData(){
        return game;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadGameMessage other = (LoadGameMessage) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		return true;
	}

}