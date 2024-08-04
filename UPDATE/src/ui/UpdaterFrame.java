package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

public class UpdaterFrame extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UpdaterFrame frame = new UpdaterFrame();
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public UpdaterFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTransferHandler(new DropFileHandler());
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
    }

    /**
     * ドロップイベント実行
     *
     * @param item
     *            受信アイテム(String)
     */
    protected void runDropEvent(Object item) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) item;

        // 一番先頭のファイルを取得
        if ((files != null) && (files.size() > 0)) {
            setDropFilePath(files.get(0).getPath());
        }

    }

    /**
     * ドロップアイテム(パス)を設定
     *
     * @param filePath
     */
    public void setDropFilePath(String filePath) {
        System.out.println(filePath);
        filePath = filePath.trim(); // 加工

        File f = new File(filePath);
        if (f.exists() == true) {
        }
    }

    /**
    *
    * ドラッグ＆ドロップハンドラー
    *
    */
   public class DropFileHandler extends TransferHandler {
       /**
        * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
        */
       @Override
       public boolean canImport(TransferSupport support) {
           if (support.isDrop() == false) {
               // ドロップ操作でない場合は受け取らない
               return false;
           }

           if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
               // ファイルでない場合は受け取らない
               return false;
           }

           return true;
       }

       /**
        * ドロップされたアイテムを受け取る
        */
       @Override
       public boolean importData(TransferSupport support) {
           // ドロップアイテム受理の確認
           if (canImport(support) == false) {
               return false;
           }

           // ドロップ処理
           Transferable t = support.getTransferable();
           try {
               // ドロップアイテム取得
               runDropEvent(t.getTransferData(DataFlavor.javaFileListFlavor));
               return true;
           }
           catch (Exception e) {
               /* 受け取らない */
           }
           return false;
       }
   }

}
