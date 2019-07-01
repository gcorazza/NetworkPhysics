package game;

import java.net.InetSocketAddress;

public class Both {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new GameServer(8080).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }

//        try {
//            new GameClient(new InetSocketAddress("localhost", 8080)).run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
