package jmp;

class InvokeThread extends Thread {

    private InvokeTask invokeTask;

    public InvokeThread(InvokeTask target) {
        super(target);
        invokeTask = target;
    }

    @Override
    public void run() {
        super.run();

        // Runtime終了
        System.exit(invokeTask.getResult() ? 0 : 1);
    }

}
