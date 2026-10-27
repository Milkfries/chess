package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private String message;
    public NotificationMessage(String notification){
        super(ServerMessageType.NOTIFICATION);
        this.message = notification;
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
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (!super.equals(obj)){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		NotificationMessage other = (NotificationMessage) obj;
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