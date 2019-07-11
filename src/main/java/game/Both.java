package game;

import java.net.InetSocketAddress;

public class Both {
    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                new GameServer(8080).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"Server Thread").start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }

        GameClient.main(null);
    }
}
