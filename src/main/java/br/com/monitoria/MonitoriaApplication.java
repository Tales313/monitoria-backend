package br.com.monitoria;

import br.com.monitoria.configurations.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class MonitoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoriaApplication.class, args);
	}

}
