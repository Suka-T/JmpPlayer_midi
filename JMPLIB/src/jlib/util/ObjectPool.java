package jlib.util;

import java.util.AbstractQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * オブジェクトプールクラス。<br>
 * インスタンスを再利用することで過度なGCを避ける。
 * 
 * @param <T> オブジェクトクラス
 */
public class ObjectPool<T> {

    /**
     * ObjectPoolクラスで提供するキューオブジェクト定義 
     */
    public static enum QueueType{
        /**
         * スレッド間で確実に同期
         */
        ArrayBlockingQueue,
        
        /**
         * スループット重視の大量処理やキューキャッシュ用途
         */
        ConcurrentLinkedQueue,
        
        /**
         * システムにまかせる場合はこれを指定。(オーバーライドしてpoolを差し替える場合など)
         * 
         */
        Auto
    }

    protected AbstractQueue<T> pool;
    private Supplier<T> newInstanceSupplier;

    public ObjectPool(int size, Supplier<T> newInstanceSupplier) {
        this.newInstanceSupplier = newInstanceSupplier;
        this.pool = createPool(QueueType.Auto, size);
        for (int i = 0; i < size; i++) {
            pool.offer(newInstance());
        }
    }
    
    public ObjectPool(QueueType queueType, int size, Supplier<T> newInstanceSupplier) {
        this.newInstanceSupplier = newInstanceSupplier;
        this.pool = createPool(queueType, size);
        for (int i = 0; i < size; i++) {
            pool.offer(newInstance());
        }
    }
    
    protected AbstractQueue<T> createPool(QueueType queueType, int size) {
        switch (queueType) {
            case ConcurrentLinkedQueue:
                return new ConcurrentLinkedQueue<>();
            case ArrayBlockingQueue:
            default:
                return new ArrayBlockingQueue<>(size);
        }
    }
    
    protected T newInstance() {
        return newInstanceSupplier.get();
    }

    public T borrow() {
        T msg = pool.poll();
        if (msg == null) {
            // 足りない場合は新しく生成（ただしGCが出る）
            msg = newInstance();
            System.out.println("Over pool");
        }
        return msg;
    }

    public void release(T msg) {
        pool.offer(msg);
    }

}
