package io.oferto.poc_docker_client.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.oferto.poc_docker_client.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("images")
public class ImageController {
	private final ImageService imageService;
	
	@Operation(summary = "Pull Image", description = "Pull image by name")
	@PostMapping	
	public ResponseEntity<Object> pullImage(
			@Parameter(description = "Image name")
			String imageName,
			@Parameter(description = "Version name")
			@RequestParam(required = false) String versionName) {
		log.debug("List all containers");
		
		Object result = imageService.pullImage(
				imageName, 
				versionName);
			
		return new ResponseEntity<Object>(result, HttpStatus.OK);			
	}
}