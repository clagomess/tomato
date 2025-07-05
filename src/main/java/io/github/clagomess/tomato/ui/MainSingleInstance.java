package io.github.clagomess.tomato.ui;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MainSingleInstance {
    private static final int INSTANCE_PORT = 35792;

    @Setter
    private Runnable bringToFront = () -> {};

    public boolean alreadyStarted(){
        log.info("check other instance is started");

        try (Socket ignored = new Socket("localhost", INSTANCE_PORT)) {
            log.info("Already started!");
            return true;
        } catch (IOException e) {
            log.debug(e.getMessage());
            initSocket();
            return false;
        }
    }

    private void initSocket(){
        new Thread(() -> {
            log.info("init socket");

            try (ServerSocket serverSocket = new ServerSocket(INSTANCE_PORT)){
                while (true) {
                    try(Socket ignored = serverSocket.accept()) {
                        log.info("hit bring to front");
                        bringToFront.run();
                    }
                }
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }, "main-single-instance").start();
    }
}
