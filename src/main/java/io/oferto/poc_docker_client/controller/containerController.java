package io.oferto.poc_docker_client.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.dockerjava.api.model.Container;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.oferto.poc_docker_client.dto.ContainerDto;
import io.oferto.poc_docker_client.service.ContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("containers")
public class ContainerController {
	private final ContainerService containerService;
		
	@Operation(summary = "List all containers", description = "Returns all containers")
	@GetMapping	
	public ResponseEntity<List<Container>> getContainers(
			@Parameter(description = "Show all containers (default shows just running)")
			@RequestParam(required = false) Boolean showAll) {
		log.debug("List all containers");
		
		List<Container> result = containerService.getContainers(showAll != null ? showAll : false);
			
		return new ResponseEntity<List<Container>>(result, HttpStatus.OK);			
	}
	
	@Operation(summary = "Submit a container", description = "Submit a container")
	@PostMapping	
	public ResponseEntity<Object> submitContainer(
			@RequestBody ContainerDto containerDto) {		
		log.debug("Submit a container");
				
		Object result = containerService.submitContainer(containerDto);
				
		return new ResponseEntity<Object>(result, HttpStatus.OK);			
	}	
	
	@Operation(summary = "Get all container logs by container Id", description = "Get all container logs by container Id")
	@GetMapping("/{containerId}")	
	public ResponseEntity<String> getLogs(
			@Parameter(description = "The container Id")
			@PathVariable String containerId) {
		log.debug("Get all container logs by container Id");
		
		String result = containerService.getContainerLogs(containerId);
			
		return new ResponseEntity<String>(result, HttpStatus.OK);			
	}
	
	@Operation(summary = "Remove container by id", description = "Remove container by Id")
	@DeleteMapping("/{containerId}")	
	public ResponseEntity<Void> removeContainer(
			@Parameter(description = "The container Id")
			@PathVariable String containerId) {
		log.debug("Remove a container by Id");
		
		containerService.removeContainer(containerId);
			
		return new ResponseEntity<Void>(HttpStatus.OK);			
	}	
}