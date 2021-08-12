package esgcoin.jminer.core.checker.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import pocminer.generate.MiningPlot;

public class ShaLibChecker {

    private final ShabalLibrary shabalLibrary;

    public ShaLibChecker() {
        shabalLibrary = Native.loadLibrary("libshabal", ShabalLibrary.class);
        shabalLibrary.shabal_init();
    }

    private interface ShabalLibrary extends Library {
        long shabal_findBestDeadline(byte[] scoops, long numScoops, byte[] gensig);
        void shabal_init();
    }

    public int findLowest(byte[] gensig, byte[] data) {
        return (int) shabalLibrary.shabal_findBestDeadline(data, data.length / MiningPlot.SCOOP_SIZE, gensig);
    }
}
