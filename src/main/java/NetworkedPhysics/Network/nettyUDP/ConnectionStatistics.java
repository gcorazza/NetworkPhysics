package NetworkedPhysics.Network.nettyUDP;

import java.util.ArrayList;
import java.util.List;

public class ConnectionStatistics {

    int maxBytePerSec;
    int packetPerSec;

    ArrayList<TimeStamped> stamps = new ArrayList<>();

    public double getPacketLossOver(int period) {
        int indexInPeriod = getStartIndex(period);

        List<TimeStamped> inTimeStamps = stamps.subList(indexInPeriod, stamps.size());

        return packetLoss(inTimeStamps);
    }

    private float packetLoss(List<TimeStamped> stamps) {
        int lowerStamp = stamps.get(0).stamp;
        int highestStamp = stamps.get(stamps.size() - 1).stamp;

        int r = howManyRounds(stamps);
        int shouldBeIn= r*255-lowerStamp+highestStamp;

        return ((float)stamps.size())/shouldBeIn;
    }

    private int howManyRounds(List<TimeStamped> stamps) {
        int lastStamp=stamps.get(0).stamp;
        int rounds=0;
        for (int i = 1; i < stamps.size(); i++) {
            int actual = stamps.get(i).stamp;
            if(lastStamp-actual>230) {
                rounds++;
            }
            lastStamp= actual;
        }
        return rounds;
    }

    private int getStartIndex(int period) {
        if (period == 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        int indexInPeriod = 0;
        for (indexInPeriod = stamps.size() - 1; indexInPeriod >= 0; indexInPeriod--) {
            if (now - stamps.get(indexInPeriod).time > period) {
                return indexInPeriod;
            }
        }
        return 0;
    }

    public void addStamp(byte stamp) {
        stamps.add(new TimeStamped(stamp));
    }


    class TimeStamped {
        long time = System.currentTimeMillis();
        byte stamp;

        public TimeStamped(byte stamp) {
            this.stamp = stamp;
        }
    }

}
