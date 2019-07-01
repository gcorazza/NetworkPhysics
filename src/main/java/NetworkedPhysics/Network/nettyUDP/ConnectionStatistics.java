package NetworkedPhysics.Network.nettyUDP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionStatistics {


//    public double getPacketLossOver(int period) {
//        List<TimeStamped> inTimeStamps = allPacketsReceivedIn(period);
//        return packetLoss(inTimeStamps);
//    }

//    private float packetLoss(List<TimeStamped> stamps) {
//        if (stamps.isEmpty())
//            return 0;
//
//        int lowerStamp = stamps.get(0).stamp;
//        int highestStamp = stamps.get(stamps.size() - 1).stamp;
//
//        int r = howManyRounds(stamps);
//        int shouldStampSize = r * 255 - lowerStamp + highestStamp + 1;
//
//        return 1f-((float) stamps.size()) / shouldStampSize;
//    }
//
//    private int howManyRounds(List<TimeStamped> stamps) {
//        int lastStamp = stamps.get(0).stamp;
//        int rounds = 0;
//        for (int i = 1; i < stamps.size(); i++) {
//            int actual = stamps.get(i).stamp;
//
//            if(lastStamp+1!=actual){
//                System.out.println("not correct");
//            }
//
//            if (lastStamp - actual > 230) {
//                rounds++;
//            }
//            lastStamp = actual;
//        }
//        return rounds;
//    }
//

    List<TimeStamped> stamps = new ArrayList<>();

    public synchronized int howManyIncorrections(int period) {
        System.out.println(Thread.currentThread().getName());
        List<TimeStamped> stamps = allPacketsReceivedIn(period);
        if (stamps.isEmpty())
            return 0;
        int lastStamp = stamps.get(0).stamp;
        int notCorrect = 0;

        for (int i = 1; i < stamps.size(); i++) {
            int actual = stamps.get(i).stamp;

            if(lastStamp+1!=actual){
                notCorrect++;
            }

            lastStamp = actual;
        }
        return notCorrect;
    }

    public synchronized int howManyPacketsReceivedIn(int period) {
        return stamps.size()-getStartIndex(period);
    }

    private List<TimeStamped> allPacketsReceivedIn(int period) {
        return stamps.subList(getStartIndex(period), stamps.size());
    }

    private int getStartIndex(int period) {
        if (period == 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        int indexInPeriod;
        for (indexInPeriod = stamps.size() - 1; indexInPeriod >= 0; indexInPeriod--) {
            if (now - stamps.get(indexInPeriod).time > period) {
                return indexInPeriod+1;
            }
        }
        return 0;
    }

    public synchronized void received(short stamp) {
        stamps.add(new TimeStamped(stamp));
    }

    class TimeStamped {
        long time = System.currentTimeMillis();
        short stamp;

        public TimeStamped(short stamp) {
            this.stamp = stamp;
        }

    }
}
