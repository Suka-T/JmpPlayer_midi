package drawLib;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawObjectEx extends DrawObject {
    protected ArrayList<DrawObjectExtender> extenders = new ArrayList<DrawObjectExtender>();
    protected VisibleFunc visibleFunc = null;

    /**
     * コンストラクタ
     */
    public DrawObjectEx() {
        super();
    }

    /**
     * 初期化
     */
    protected void initialize() {
        super.initialize();
    }

    /**
     * 拡張描画クラスを追加
     *
     * @param extender
     *            拡張描画クラス
     */
    public void addDrawObjectExtender(DrawObjectExtender extender) {
        this.extenders.add(extender);
    }

    /**
     * 登録コンポーネント取得
     *
     * @return
     */
    public List<DrawObjectExtender> getDrawObjectExtenders() {
        return extenders;
    }

    /**
     * 可視状態メソッドの上書き
     *
     * @param func
     */
    public void setVisibleFunc(VisibleFunc func) {
        visibleFunc = func;
    }

    @Override
    public boolean isVisible() {
        if (visibleFunc != null) {
            return visibleFunc.isVisible();
        }
        return super.isVisible();
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        drawExtender(g);
    }

    /**
     * 拡張描画処理
     *
     * @param g
     *            グラフィックスクラス
     */
    protected void drawExtender(Graphics g) {
        if (isVisible() == true) {
            for (DrawObjectExtender ex : extenders) {
                ex.draw(g);
            }
        }
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        // マウスイベント用のeventin
        boolean ret = false;
        if (eventIn(e) == true) {
            if (mousePressedExtender(e) == true) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        // マウスイベント用のeventin
        boolean ret = false;
        if (eventIn(e) == true) {
            if (mouseReleasedExtender(e) == true) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        // マウスイベント用のeventin
        boolean ret = false;
        if (eventIn(e) == true) {
            if (mouseClickedExtender(e) == true) {
                ret = true;
            }
        }
        return ret;
    }

    protected boolean mousePressedExtender(MouseEvent e) {
        if (eventIn(e) == true) {
            for (DrawObjectExtender ex : extenders) {
                if (ex.mousePressed(e) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean mouseReleasedExtender(MouseEvent e) {
        if (eventIn(e) == true) {
            for (DrawObjectExtender ex : extenders) {
                if (ex.mouseReleased(e) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean mouseClickedExtender(MouseEvent e) {
        if (eventIn(e) == true) {
            for (DrawObjectExtender ex : extenders) {
                if (ex.mouseClicked(e) == true) {
                    return true;
                }
            }
        }
        return false;
    }
}
