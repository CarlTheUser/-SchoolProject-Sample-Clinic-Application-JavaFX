package core.model.readonly;

import core.model.Room;
import core.model.RoomType;
import javafx.beans.property.*;

public class RoomUsage {

    private final ReadOnlyObjectWrapper<Room> room = new ReadOnlyObjectWrapper<>(this, "room", null);
    private final ReadOnlyIntegerWrapper bedsInUse = new ReadOnlyIntegerWrapper(this, "bedsInUse", 0);

    public static RoomUsage FromDb(int id, RoomType type, int bedCount, double hourlyRate, String locationDetails, int bedsInUse){
        Room room = Room.FromDb(id, type, bedCount, hourlyRate, locationDetails);
        RoomUsage r = new RoomUsage(room, bedsInUse);
        return r;
    };

    private RoomUsage(Room room, int bedsInUse){
        setRoom(room);
        setBedsInUse(bedsInUse);
    }

    public Room getRoom() {
        return room.get();
    }

    public ReadOnlyObjectProperty<Room> roomProperty() {
        return room.getReadOnlyProperty();
    }

    public void setRoom(Room room) {
        this.room.set(room);
    }

    public int getBedsInUse() {
        return bedsInUse.get();
    }

    public ReadOnlyIntegerProperty bedsInUseProperty() {
        return bedsInUse.getReadOnlyProperty();
    }

    public void setBedsInUse(int bedsInUse) {
        this.bedsInUse.set(bedsInUse);
    }
}
