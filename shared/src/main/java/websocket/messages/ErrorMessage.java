package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String message;
    public ErrorMessage(String error){
        super(ServerMessageType.ERROR);
        this.message = error;
    }
    public String getMessage(){
        return message;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object object) {
		if (this == object){
			return true;
		}
		if (object == null){
			return false;
		}
		if (!super.equals(object)){
			return false;
		}
		if (getClass() != object.getClass()){
			return false;
		}
		ErrorMessage other = (ErrorMessage) object;
		if (message == null) {
			if (other.message != null){
				return false;
			}
		} else if (!message.equals(other.message)){
			return false;
		}
			
		return true;
	}

}