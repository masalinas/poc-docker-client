package io.oferto.poc_docker_client.service;

import java.util.ArrayList;
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
public class ContainerService {
	private final DockerClient dockerClient;

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
		Map<String, Object> result = new HashMap<>();
		
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
	                    
	                	result.put("info", container);
	                }
	            }
	        }
        } catch (DockerException exception) {
        	log.error(exception.getStackTrace().toString());
        	
        	result.put("error", exception.getMessage()); 
        } catch (Exception exception) {
        	log.error("Unexpected error: " + exception.getMessage());
        	
        	result.put("error", exception.getMessage()); 
        }
		
		return null;		
	}
	
	public Object runContainer(ContainerDto containerDto) {
		Map<String, String> result = new HashMap<>();
		
		try {	   
			// STEP01: pull the image if not exist and wait for it
			dockerClient
					.pullImageCmd(containerDto.getImageName() + ":" + (containerDto.getImageVersion() != null ? containerDto.getImageVersion() : "latest"))
		            		.start()
		            		.awaitCompletion();
			
			// STEP02: create docker command
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
		
			// configure container resources (cpu an memory)
			if (containerDto.getContainerResourcesDto() != null) {
				hostConfig.withCpuCount(containerDto.getContainerResourcesDto().getCpuCount());
								
				if (containerDto.getContainerResourcesDto().getCpuPeriod() != null)
					hostConfig.withCpuPeriod(containerDto.getContainerResourcesDto().getCpuPeriod() * 1000);
				
				if (containerDto.getContainerResourcesDto().getCpuQuota() != null)
					hostConfig.withCpuQuota(containerDto.getContainerResourcesDto().getCpuQuota() * 1000);
				
				if (containerDto.getContainerResourcesDto().getMemory() != null)
					hostConfig.withMemory(containerDto.getContainerResourcesDto().getMemory() * 1024 * 1024L);
			}
			
			// create run docker command to be executed
	        CreateContainerResponse containerResponse = dockerClient
	        		.createContainerCmd(containerDto.getImageName() + ":" + containerDto.getImageVersion())
	                .withName(containerDto.getContainerName())                
	                .withHostConfig(hostConfig)
	                .withExposedPorts(exposedPorts)
		            //.withCmd("echo", "Goodbye")
	                .exec(); 		
	        
	        // STEP03: start the container just created
	        dockerClient
	        	.startContainerCmd(containerResponse.getId())
	        	.exec();
	        
            result.put("message", "Container with id " + containerResponse.getId() + " created");
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