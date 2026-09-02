package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String error;
    public ErrorMessage(String error){
        super(ServerMessageType.ERROR);
        this.error = error;
    }
    public String getNotfication(){
        return error;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((error == null) ? 0 : error.hashCode());
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
		ErrorMessage other = (ErrorMessage) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		return true;
	}

}