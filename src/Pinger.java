import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Pinger implements Runnable {
    private Socket socket;
    private String expectedResponse = "PONG";
    Thread thread = new Thread();

    public Pinger(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            while (true) {
                try {
                    thread.sleep(60000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println("PING");
                    out.flush();

                    try {
                        thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!in.equals(expectedResponse)) {
                        thread.interrupt();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


