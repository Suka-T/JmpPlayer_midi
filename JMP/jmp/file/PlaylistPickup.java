package jmp.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import function.Utility;
import jmp.core.DataManager;
import jmp.core.FileManager;
import jmp.core.JMPCore;

public class PlaylistPickup {

    private File dir;
    private List<File> pool;
    private List<File> played;

    public PlaylistPickup() {
        pool = new ArrayList<File>();
        played = new ArrayList<File>();
    }

    public File getDir() {
        return dir;
    }

    public void remakePool() {

        FileManager fm = JMPCore.getFileManager();
        DataManager dm = JMPCore.getDataManager();

        List<File> tmp = new ArrayList<File>();
        pool.clear();
        played.clear();

        dir = new File(dm.getPlayListPath());

        String cur = Utility.getFileNameAndExtension(dm.getLoadedFile());

        /* カレントディレクトリのファイルを抽出 */
        Map<String, File> map = fm.getFileMap(dir);
        for (int i = 0; i < fm.getFileListModel().getRowCount(); i++) {
            String name = fm.getFileListModel().getValueAt(i, 1).toString();
            File file = map.get(name);
            if (file.exists() == false || file.isFile() == false || file.canRead() == false) {
                continue;
            }

            if (cur.equals(Utility.getFileNameAndExtension(file)) == false) {
                tmp.add(file);
            }
        }

        if (tmp.isEmpty() == true) {
            return;
        }

        /* 候補リストを作成 */
        if (dm.isRandomPlay() == false) {
            for (File f : tmp) {
                pool.add(f);
            }
        }
        else {
            Random random = new Random();
            while (tmp.isEmpty() == false) {
                int ranValue = random.nextInt(tmp.size());
                pool.add(tmp.remove(ranValue));
            }
        }
    }

    public void sync(File file) {
        String name = Utility.getFileNameAndExtension(file);
        remakePool();

        /* 指定したファイルとリストのポインタを位相合わせ */
        boolean exists = false;
        while (pool.isEmpty() == false) {
            File f = next();
            if (name.equals(Utility.getFileNameAndExtension(f)) == true) {
                exists = true;
                break;
            }
        }

        if (exists == false) {
            remakePool();
        }
        return;
    }

    public File next() {
        if (pool.isEmpty() == true) {
            remakePool();
            if (pool.isEmpty() == true) {
                return null;
            }
        }

        File next = pool.remove(0);
        played.add(0, next);
        return next;
    }

    public File prev() {
        if (played.isEmpty() == true) {
            return null;
        }

        // 現在再生しているファイルを抽出
        File prev = played.remove(0);
        pool.add(0, prev);
        if (played.isEmpty() == false) {
            // 前のファイルを戻す
            prev = played.remove(0);
        }
        return prev;
    }

}
