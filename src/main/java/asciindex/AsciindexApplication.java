package asciindex;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableAutoConfiguration
public class AsciindexApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsciindexApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();

	}
}
