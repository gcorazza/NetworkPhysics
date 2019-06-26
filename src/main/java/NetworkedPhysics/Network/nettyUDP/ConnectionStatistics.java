package NetworkedPhysics.Network.nettyUDP;

import java.util.ArrayList;
import java.util.List;

public class ConnectionStatistics {

    int maxBytePerSec;
    int packetPerSec;

    ArrayList<TimeStamped> stamps = new ArrayList<>();

    public double getPacketLossOver(int period) {
        List<TimeStamped> inTimeStamps = allPacketsReceivedIn(period);
        return packetLoss(inTimeStamps);
    }

    public int howManyPacketsReceivedIn(int period){
        List<TimeStamped> timeStampeds = allPacketsReceivedIn(period);
        return timeStampeds.size();
    }
    private List<TimeStamped> allPacketsReceivedIn(int period){
        return stamps.subList(getStartIndex(period), stamps.size());
    }


    private float packetLoss(List<TimeStamped> stamps) {
        if (stamps.isEmpty())
            return 0;

        int lowerStamp = stamps.get(0).stamp;
        int highestStamp = stamps.get(stamps.size() - 1).stamp;

        int r = howManyRounds(stamps);
        int shouldStampSize = r * 255 - lowerStamp + highestStamp + 1;

        return 1f-((float) stamps.size()) / shouldStampSize;
    }

    private int howManyRounds(List<TimeStamped> stamps) {
        int lastStamp = stamps.get(0).stamp;
        int rounds = 0;
        for (int i = 1; i < stamps.size(); i++) {
            int actual = stamps.get(i).stamp;

            if(lastStamp+1!=actual){
                System.out.println("not correct");
            }

            if (lastStamp - actual > 230) {
                rounds++;
            }
            lastStamp = actual;
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
