package io.oferto.poc_docker_client.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.PullResponseItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class ImageService {
	private final DockerClient dockerClient;
	
	public Object pullImage(String imageName, String imageVersion) {
		Map<String, String> result = new HashMap<>();
		
		try {	        
			Adapter<PullResponseItem> pullResponseItem = dockerClient
					.pullImageCmd(imageName + ":" + (imageVersion != null ? imageVersion : "latest"))
		            		.start();
		            		//.awaitCompletion();
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());          
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        }	
		
		return result;
	}
}