package io.oferto.poc_docker_client.service;

import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class ImageService {
	private final DockerClient dockerClient;
	
	public ResponseEntity<String> pullImage(String imageName, String imageVersion) {
		
		try (PullImageCmd pullCmd = dockerClient.pullImageCmd(imageName).withTag(imageVersion != null ? imageVersion : "latest")) {
			final StringBuilder statusBuilder = new StringBuilder();
			
			PullImageResultCallback callback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    if (item.getStatus() != null) {                    
                    	log.info("Status: " + item.getStatus());
                    	
                        statusBuilder.append("Status: ").append(item.getStatus()).append("\n");
                    }
                    
                    if (item.getErrorDetail() != null) {
                    	log.error("Error: " + item.getErrorDetail().getMessage());
                    	
                        statusBuilder.append("Error: ").append(item.getErrorDetail().getMessage()).append("\n");
                    }
                    
                    super.onNext(item);
                }

                @Override
                public void onError(Throwable throwable) {
                	log.error("Pull failed: " + throwable.getMessage());
                    
                    statusBuilder.append("Pull failed: ").append(throwable.getMessage()).append("\n");

                    super.onError(throwable);
                }

                @Override
                public void onComplete() {
                	log.info("Pull completed successfully.");
                    
                    statusBuilder.append("Pull completed successfully.\n");

                    super.onComplete();
                }
            };

            pullCmd.exec(callback).awaitCompletion(120, TimeUnit.SECONDS);
            
            return ResponseEntity.ok(statusBuilder.toString());
        } catch (Exception e) {
        	log.error("Exception during pull: " + e.getMessage());
        	
            return ResponseEntity.internalServerError().body("Error pulling image: " + e.getMessage());
        }
	}
}