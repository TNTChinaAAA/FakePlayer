package net.tntchina.fakeplayer;

public class SystemGCRunnable implements Runnable {

    public static long lastTime = System.currentTimeMillis();

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        double percentage = (double) runtime.freeMemory() / runtime.maxMemory();
        if (percentage < 0.15) {
            System.gc();
            //FakePlayer.logger.info("Memory Garbage collected.");
        }
    }

    @Deprecated
    public void repeatClean() {
        long lt = System.currentTimeMillis();
        if (lt - SystemGCRunnable.lastTime >= 20000L) {
            System.gc();
            FakePlayer.logger.info("Garbage collected.");
            SystemGCRunnable.lastTime = System.currentTimeMillis();
        }
    }
}
