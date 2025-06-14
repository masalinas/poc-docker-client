package io.oferto.poc_docker_client.configure;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI myOpenAPI() {
		Server devServer = new Server();
		devServer.setUrl("http://localhost:8080");
		devServer.setDescription("Server URL in Development environment");

		Server prodServer = new Server();
		prodServer.setUrl("http://localhost:8080");
		prodServer.setDescription("Server URL in Production environment");

		Contact contact = new Contact();
		contact.setEmail("masalinas.gancedo@gmail.com");
		contact.setName("Miguel Ángel Salinas Gancedo");
		contact.setUrl("http://isa.uniovi.es/GSDPI/");

		License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
		
		Info info = new Info().title("Container Docker API")
				.version("1.0.0")
				.contact(contact)
				.description("This API exposes endpoints to manage Docker containers.")
				.license(mitLicense);
		
		return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
	}
}