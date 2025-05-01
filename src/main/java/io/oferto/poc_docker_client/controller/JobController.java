package io.oferto.poc_docker_client.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.dockerjava.api.model.Container;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.oferto.poc_docker_client.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("jobs")
public class JobController {
	private final JobService jobService;
	
	@Operation(summary = "Get all jobs", description = "Returns all jobs created")
	@GetMapping	
	public ResponseEntity<List<Container>> getContainers() {
		log.debug("getContainers job");
		
		List<Container> result = jobService.getJobs();
			
		return new ResponseEntity<List<Container>>(result, HttpStatus.OK);			
	}
	
	@Operation(summary = "Submit a job", description = "Submit a job")
	@PostMapping("/{jobName}/images/{imageName}/ports/{exposedPort}/{bindPort}")	
	public ResponseEntity<Object> submitJob(
			@PathVariable String jobName,
			@PathVariable String imageName,
			@PathVariable int exposedPort,
			@PathVariable int bindPort) {
		log.debug("submitJob job");
		
		Object result = jobService.submitJob(
				jobName,
				imageName,
				exposedPort,
				bindPort);
			
		return new ResponseEntity<Object>(result, HttpStatus.OK);			
	}	
	
	@Operation(summary = "Get all job logs", description = "Get all job logs")
	@GetMapping("/{jobId}")	
	public ResponseEntity<String> getLogs(
			@Parameter(description = "The job id")
			@PathVariable String jobId) {
		log.debug("getLogs job");
		
		String result = jobService.getJobLogs(jobId);
			
		return new ResponseEntity<String>(result, HttpStatus.OK);			
	}
	
	@Operation(summary = "Remove job By id", description = "Remove job By id")
	@DeleteMapping("/{jobId}")	
	public ResponseEntity<Void> removeJob(
			@Parameter(description = "The job id")
			@PathVariable String jobId) {
		log.debug("removeJob job");
		
		jobService.removeJob(jobId);
			
		return new ResponseEntity<Void>(HttpStatus.OK);			
	}	
}