package websocket.commands;

public class MakeMoveCommand extends UserGameCommand{
    private String startPosition;
    private String endPosition;

    public MakeMoveCommand(String authToken, Integer gameID, String startPosition, String endPosition) {
        super(CommandType.MAKE_MOVE, authToken,gameID);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getStartPosition(){
        return startPosition;
    }
    public String getEndPosition(){
        return endPosition;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((startPosition == null) ? 0 : startPosition.hashCode());
        result = prime * result + ((endPosition == null) ? 0 : endPosition.hashCode());
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
        MakeMoveCommand other = (MakeMoveCommand) obj;
        if (startPosition == null) {
            if (other.startPosition != null)
                return false;
        } else if (!startPosition.equals(other.startPosition))
            return false;
        if (endPosition == null) {
            if (other.endPosition != null)
                return false;
        } else if (!endPosition.equals(other.endPosition))
            return false;
        return true;
    }

    

}
