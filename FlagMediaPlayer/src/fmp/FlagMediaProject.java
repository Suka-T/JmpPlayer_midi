package fmp;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.MediaPlayer;

public class FlagMediaProject {

    List<FlagMediaEvent> lst = new ArrayList<FlagMediaEvent>();

    public FlagMediaProject() {
    }

    public void addEvent(FlagMediaEvent event) {
        lst.add(event);
    }

    public void execute(MediaPlayer mp) {
        for (FlagMediaEvent fme : lst) {
            if (fme.isExecute(mp) == true) {
                fme.execute(mp);
            }
        }
    }

    public void trrigerEvent(MediaPlayer mp) {
        for (FlagMediaEvent fme : lst) {
            if (fme.isTrriger(mp) == true) {
                fme.trrigerEvent(mp);
            }
        }
    }

    public void onMouseEvent(MouseEvent e) {
        for (FlagMediaEvent fme : lst) {
            fme.onMouseEvent(e);
        }
    }

}
