package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

public class SSHProxyWrapper {
    private static final String LOCALHOST = "127.0.0.1";

    public ResponseDto.Response wrap(
            ProxyDto proxy,
            URI uri,
            HttpPeformRunnable httpPeform
    ) throws Exception {
        try (SSHClient ssh = new SSHClient()){
            ssh.loadKnownHosts();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(proxy.getHost(), proxy.getPort());
            ssh.authPassword(proxy.getUsername(), proxy.getPassword());

            try (ServerSocket serverSocket = new ServerSocket()){
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(LOCALHOST, 0));
                int localPort = serverSocket.getLocalPort();

                LocalPortForwarder forwarder = ssh.newLocalPortForwarder(new Parameters(
                        LOCALHOST,
                        localPort,
                        uri.getHost(),
                        getURIPort(uri)
                ), serverSocket);

                Thread thread = new Thread(() -> {
                    try {
                        forwarder.listen();
                    } catch (IOException ignored) {}
                }, "sshj-local-forward");
                thread.setDaemon(true);
                thread.start();

                return httpPeform.apply(changeURIPort(uri, localPort));
            }
        }
    }

    protected int getURIPort(URI uri){
        int port = uri.getPort();
        if(port <= 0 && uri.getScheme().equals("https")) return 443;
        if(port <= 0 && uri.getScheme().equals("http")) return 80;
        return port;
    }

    protected URI changeURIPort(URI uri, int localPort) throws URISyntaxException {
        return new URI(
                uri.getScheme(),
                uri.getUserInfo(),
                uri.getHost(),
                localPort,
                uri.getPath(),
                uri.getQuery(),
                uri.getFragment()
        );
    }

    @FunctionalInterface
    public interface HttpPeformRunnable {
        ResponseDto.Response apply(URI value) throws Exception;
    }
}
