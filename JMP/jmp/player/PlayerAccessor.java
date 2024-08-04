package jmp.player;

import java.util.ArrayList;
import java.util.List;

import jlib.player.Player;

public class PlayerAccessor {

    private List<Player> registerPlayer = new ArrayList<Player>();
    private Player currentPlayer = null;

    public PlayerAccessor() {
    }

    public boolean open() {
        boolean ret = true;
        for (Player p : registerPlayer) {
            ret = p.open();
            if (ret == false) {
                break;
            }
        }
        return ret;
    }

    public boolean close() {
        boolean ret = true;
        stopAllPlayer();
        for (Player p : registerPlayer) {
            boolean tmp = ret;
            ret = p.close();
            if (tmp == false) {
                ret = false;
            }
        }
        return ret;
    }

    public List<Player> getPlayers() {
        return registerPlayer;
    }

    public void stopAllPlayer() {
        for (Player p : registerPlayer) {
            p.stop();
        }
    }

    public void register(Player player) {
        if (registerPlayer.contains(player) == false) {
            registerPlayer.add(player);
        }
    }

    public void remove(Player player) {
        if (registerPlayer.contains(player) == true) {
            registerPlayer.remove(player);
        }
    }

    public boolean change(String extension) {
        Player player = getSupportedPlayer(extension);
        if (player != null) {
            change(player);
            return true;
        }
        return false;
    }

    public void change(Player player) {
        if (registerPlayer.contains(player) == true) {
            if (currentPlayer != null) {
                if (currentPlayer.isRunnable() == true) {
                    currentPlayer.stop();
                }
            }
            currentPlayer = player;
        }
    }

    public Player getCurrent() {
        return currentPlayer;
    }

    private Player getSupportedPlayer(String extension) {
        Player supportedPlayer = null;
        for (Player player : registerPlayer) {
            if (player.isAllSupported() == true) {
                supportedPlayer = player;
            }
            if (player.isSupportedExtension(extension) == true) {
                // 限定プレイヤーを優先して使用する
                supportedPlayer = player;
                break;
            }
        }
        return supportedPlayer;
    }

    public boolean isSupportedExtension(String extension) {
        for (Player player : registerPlayer) {
            if (player.isAllSupported() == true) {
                return true;
            }
            if (player.isSupportedExtension(extension) == true) {
                return true;
            }
        }
        return false;
    }

    public boolean isValid() {
        Player player = getCurrent();
        if (player == null) {
            return false;
        }
        return player.isValid();
    }

}
