package io.oferto.poc_docker_client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.PullResponseItem;

import io.oferto.poc_docker_client.dto.ContainerDto;
import io.oferto.poc_docker_client.dto.ContainerPortDto;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class containerService {
	private final DockerClient dockerClient;
	
	public Adapter<PullResponseItem> pullImage(String imageName, String imageVersion) {
		Adapter<PullResponseItem> pullResponseItem = null;
		
		try {	        
			pullResponseItem = dockerClient
					.pullImageCmd(imageName + ":" + imageVersion)
		            		.start();
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());          
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        }	
		
		return pullResponseItem;
	}
	
	public List<Container> getContainers(boolean showAll) {
		try {	        
			return dockerClient.listContainersCmd()
					.withShowAll(showAll)
					.exec();
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());          
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        }
		
		return null;		
	}	
	
	public Container getContainerByName(String containerName) {
		try {	        
	        List<Container> containers = dockerClient.listContainersCmd()
	                .withShowAll(true)  // include stopped containers
	                .exec();

	        for (Container container : containers) {
	            String[] names = container.getNames();  // e.g., ["/my-container"]
	            for (String name : names) {
	                if (name.equals("/" + containerName)) {
	                	log.info("Found container ID: " + container.getId());
	                	log.info("Status: " + container.getStatus());
	                    
	                	return container;
	                }
	            }
	        }
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());          
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        }
		
		return null;		
	}
	
	public Object submitContainer(ContainerDto containerDto) {
		Map<String, String> result = new HashMap<>();
		
		try {	        
			HostConfig hostConfig = HostConfig.newHostConfig()
					.withAutoRemove(containerDto.getWithAutoRemove());
			
			List<ExposedPort> exposedPorts = new ArrayList<ExposedPort>();
			if (containerDto.getPublishPorts().size() > 0) {
				Ports portBindings = new Ports();
				
				for (ContainerPortDto port : containerDto.getPublishPorts()) {
					PortBinding portBinding = new PortBinding(
							Ports.Binding.bindPort(port.getHostPort()), 
							ExposedPort.tcp(port.getContainerPort()));
					
					portBindings.add(portBinding);
					exposedPorts.add(new ExposedPort(port.getHostPort()));
		        }
				
				hostConfig.withPortBindings(portBindings);
			}
		
	        CreateContainerResponse container = dockerClient
	        		.createContainerCmd(containerDto.getImageName() + ":" + containerDto.getImageVersion())
	                .withName(containerDto.getContainerName())                
	                .withHostConfig(hostConfig)
	                .withExposedPorts(exposedPorts)	
		            //.withCmd("echo", "Goodbye")
	                .exec(); 		
	        
	        // STEP03: start the container just created
	        dockerClient
	        	.startContainerCmd(container.getId())
	        	.exec();
	        
            result.put("message", "Container with id " + container.getId() + " created");
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());
            
            result.put("error", exception.getMessage());           
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        	
        	result.put("error", exception.getMessage());
        }	
		
		return result;
	}
	
	public String getContainerLogs(String containerId) {
		StringBuilder logs = new StringBuilder();
		
		try {	     
			dockerClient.logContainerCmd(containerId)
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
	
	public void removeContainer(String containerId) {		
		try {	     
			dockerClient.removeContainerCmd(containerId)
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