package exceptions;
import java.util.ArrayList;

public class ProgramManagerException extends Exception {
	public ArrayList<String> messages = new ArrayList<String>();
    public  ProgramManagerException(String errMsg) {
        super(errMsg);
    }
    public void addMessageToStack(String msg) {
        this.messages.add(msg);
    }
    public void printStack() {
        for (String msg: this.messages) {
            System.out.println(msg);
        }
    }
}