package io.oferto.poc_docker_client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class JobService {
	private final DockerClient dockerClient;
	
	public List<Container> getJobs() {
		try {	        
			return dockerClient.listContainersCmd().exec();
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());          
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        }
		
		return null;		
	}	
	
	public Object submitJob(String jobName, String imageName, int exposedPort, int bindPort) {
		Map<String, String> result = new HashMap<>();
		
		try {
    		// STEP01: pull image
			dockerClient
				.pullImageCmd(imageName)
            		.start()
            		.awaitCompletion();
	        
			// STEP02: create job from image
	        CreateContainerResponse container = dockerClient
	        	.createContainerCmd(imageName)
	                .withName(jobName)
	                .withExposedPorts(new ExposedPort(exposedPort))
	                .withHostConfig(HostConfig.newHostConfig()
	                		.withAutoRemove(true)
	                		.withPortBindings(new Ports(new ExposedPort(exposedPort), Ports.Binding.bindPort(bindPort))))
		            //.withCmd("echo", "Goodbye")
	                .exec(); 		
	        
	        // STEP03: start the job just created
	        dockerClient
	        	.startContainerCmd(container.getId())
	        	.exec();
	        
            result.put("message", "Job with id " + container.getId() + " created");
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());
            
            result.put("error", exception.getMessage());           
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        	
        	result.put("error", exception.getMessage());
        }	
		
		return result;
	}	
 	
	public String getJobLogs(String jobId) {
		StringBuilder logs = new StringBuilder();
		
		try {	     
			dockerClient.logContainerCmd(jobId)
				.withStdOut(true)
	            .withStdErr(true)
	            .withTailAll()
	            .withFollowStream(false)
	            .exec(new ResultCallback.Adapter<Frame>() {
	                @Override
	                public void onNext(Frame frame) {
	                	logs.append(new String(frame.getPayload()));
	                }
	            }).awaitCompletion();          
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());    
        	
        	throw exception;
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        	
        	throw new RuntimeException("Failed to read logs: " + exception.getMessage(), exception);
        }
		
		return logs.toString();
	}	
	
	public void removeJob(String jobId) {		
		try {	     
			dockerClient.removeContainerCmd(jobId)
				.withForce(true)
				.exec();   
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());    
        	
        	throw exception;
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        	
        	throw new RuntimeException("Failed to read logs: " + exception.getMessage(), exception);
        }	
	}
}