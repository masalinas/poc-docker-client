package io.oferto.poc_docker_client.configure;

import java.io.IOException;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

@Configuration
public class DockerClientConfig {
	final int MAX_CONNECTIONS = 100;
	final int CONNECTION_TIMEOUT = 30;
	final int RESPONSE_TIMEOUT = 45;

    @Bean
	public DockerClient getDockerClient() throws IOException {
    	DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
    			.withDockerHost("unix:///var/run/docker.sock")  // Local Docker daemon (Linux/macOS)
    			//.withApiVersion("1.41")    			
                //.withDockerHost("tcp://localhost:2375")       // Use for Docker Desktop (Windows or remote Docker)
    			//.withDockerTlsVerify(true)
    			//.withDockerCertPath("/home/user/.docker")
    		    .withRegistryUsername("gsdpi")
    		    .withRegistryPassword("!Thingtrack2010")
    		    .withRegistryEmail("uo34525@uniovi.es")
    		    .withRegistryUrl("https://index.docker.io/v1/")
    		.build();

    	DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
    		    .dockerHost(config.getDockerHost())
    		    .sslConfig(config.getSSLConfig())
    		    .maxConnections(MAX_CONNECTIONS)
    		    .connectionTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
    		    .responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT))
    		.build();

		return DockerClientImpl.getInstance(config, httpClient);
	}
}
