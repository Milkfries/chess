package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private String errorMessage;
    public ErrorMessage(String error){
        super(ServerMessageType.ERROR);
        this.errorMessage = error;
    }
    public String getMessage(){
        return errorMessage;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
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
		if (errorMessage == null) {
			if (other.errorMessage != null){
				return false;
			}
		} else if (!errorMessage.equals(other.errorMessage)){
			return false;
		}
			
		return true;
	}

}