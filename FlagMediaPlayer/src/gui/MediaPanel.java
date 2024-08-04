package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import fmp.FlagMediaProject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

public class MediaPanel extends JFXPanel {

    protected Media media = null;
    protected MediaPlayer player = null;
    protected FlagMediaProject fmp = null;
    protected Thread th;
    protected boolean isRunnable = true;
    protected boolean isAudioOnly = false;

    public MediaPanel(File file, FlagMediaProject fmp) {
        this.fmp = fmp;

        StackPane root = new StackPane();
        Background bg = new Background(new BackgroundFill(Color.BLACK, null, null));
        root.setBackground(bg);

        media = new Media(file.toURI().toString());
        player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        root.getChildren().add(mediaView);

        // シーン作成
        Scene scene = new Scene(root);

        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();
        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        setScene(scene);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (fmp != null) {
                    fmp.onMouseEvent(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (fmp != null) {
                    fmp.onMouseEvent(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (fmp != null) {
                    fmp.onMouseEvent(e);
                }
            }
        });
    }

    public void threadStart() {
        if (fmp != null) {
            th = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isRunnable) {
                        fmp.execute(getPlayer());
                        fmp.trrigerEvent(getPlayer());
                    }
                }
            });
            th.start();
        }
    }

    public Media getMedia() {
        return media;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setAudioOnly(boolean b) {
        isAudioOnly = b;
    }

    public boolean isAudioOnly() {
        return isAudioOnly;
    }

    public void exit() {
        isRunnable = false;
        if (th != null) {
            try {
                th.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (player != null) {
            player.dispose();
        }
    }

}
