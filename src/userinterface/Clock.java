package userinterface;

import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {

    Listener listener;

    private final Timer timer;

    public Clock(long updateInterval) {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(listener != null) listener.onClockUpdated(LocalDateTime.now());
            }
        }, 0, updateInterval);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onClockUpdated(LocalDateTime currentTime);
    }

}
