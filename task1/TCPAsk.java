//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;
import tcpclient.TCPClient;

public class TCPAsk {
    static boolean shutdown = false;
    static Integer timeout = null;
    static Integer limit = null;
    static String hostname = null;
    static int port = 0;
    static byte[] userInputBytes = new byte[0];

    public TCPAsk() {
    }

    private static void usage() {
        System.err.println("Usage: TCPAsk [options] host port <data to server>");
        System.err.println("Possible options are:");
        System.err.println("    --shutdown");
        System.err.println("    --timeout <milliseconds>");
        System.err.println("    --limit <bytes>");
        System.exit(1);
    }

    private static void parseArgs(String[] var0) {
        try {
            int var1;
            for(var1 = 0; var1 < var0.length && var0[var1].startsWith("--"); ++var1) {
                if (var0[var1].equals("--shutdown")) {
                    shutdown = true;
                } else if (var0[var1].equals("--timeout")) {
                    ++var1;
                    timeout = Integer.parseInt(var0[var1]);
                } else if (var0[var1].equals("--limit")) {
                    ++var1;
                    limit = Integer.parseInt(var0[var1]);
                } else {
                    usage();
                }
            }

            hostname = var0[var1++];
            port = Integer.parseInt(var0[var1++]);
            if (var1 < var0.length) {
                StringBuilder var2 = new StringBuilder();

                for(boolean var3 = true; var1 < var0.length; var2.append(var0[var1++])) {
                    if (var3) {
                        var3 = false;
                    } else {
                        var2.append(" ");
                    }
                }

                var2.append("\n");
                userInputBytes = var2.toString().getBytes();
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException var4) {
            usage();
        }

    }

    public static void main(String[] var0) {
        parseArgs(var0);

        try {
            TCPClient var1 = new TCPClient(shutdown, timeout, limit);
            byte[] var2 = var1.askServer(hostname, port, userInputBytes);
            String var3 = new String(var2);
            System.out.printf("%s:%d says:\n%s", hostname, port, var3);
            if (var3.length() > 0 && !var3.endsWith("\n")) {
                System.out.println();
            }
        } catch (IOException var4) {
            System.err.println(var4);
            System.exit(1);
        }

    }
}
