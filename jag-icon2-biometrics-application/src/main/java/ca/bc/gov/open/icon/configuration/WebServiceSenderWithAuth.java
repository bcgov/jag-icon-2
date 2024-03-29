package ca.bc.gov.open.icon.configuration;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

@Configuration
public class WebServiceSenderWithAuth extends HttpUrlConnectionMessageSender {

    @Value("${security.web-service.username}")
    private String username;

    @Value("${security.web-service.password}")
    private String password;

    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {
        String input = username + ":" + password;
        String auth = Base64Utils.encodeToString(input.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + auth);

        super.prepareConnection(connection);
    }
}
