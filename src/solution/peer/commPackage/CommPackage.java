package solution.peer.commPackage;

public class CommPackage {

    private int senderGUID;
    private String message;
    private PackageType type;

    public CommPackage(int GUID, String message, PackageType type){
        this.message = message;
        this.senderGUID = GUID;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderGUID() {
        return senderGUID;
    }

    public void setSenderGUID(int senderGUID) {
        this.senderGUID = senderGUID;
    }
}
