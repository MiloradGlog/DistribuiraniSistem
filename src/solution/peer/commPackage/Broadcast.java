package solution.peer.commPackage;

import solution.peer.NodeInfo;

public class Broadcast {

    private String broadcastID;
    private String message;

    public Broadcast(int senderGUID, String message){
        this.broadcastID = Integer.toString(senderGUID) + System.currentTimeMillis();
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Broadcast){
            if (((Broadcast)obj).getBroadcastID().equals(this.broadcastID)){
                return true;
            }
            else {
                return false;
            }
        }
        return super.equals(obj);
    }

    public String getBroadcastID() {
        return broadcastID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Broadcast{" +
                "broadcastID='" + broadcastID + '}';
    }
}
