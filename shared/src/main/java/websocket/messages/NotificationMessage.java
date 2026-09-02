package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private String notification;
    public NotificationMessage(String notification){
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }
    public String getNotfication(){
        return notification;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((notification == null) ? 0 : notification.hashCode());
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
		NotificationMessage other = (NotificationMessage) obj;
		if (notification == null) {
			if (other.notification != null)
				return false;
		} else if (!notification.equals(other.notification))
			return false;
		return true;
	}

}