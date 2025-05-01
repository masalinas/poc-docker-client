package io.oferto.poc_docker_client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContainerResourcesDto {
	@Schema(description = "Number of CPUs to dedicate (hard limit). Example: 2 -> 2 total CPU core limits")
	Long cpuCount;
	@Schema(description = "The time window for CPU scheduling (in miliseconds). Example 100 -> default 100ms")
	Long cpuPeriod;
	@Schema(description = "Total available CPU time in the period (in miliseconds). Example 50 -> 50 ms of 100 ms total then container gets 50% of 1 CPU")
	Long cpuQuota;
	@Schema(description = "Memory limit in MB. Example 250 -> 250MB memory total memory limit")
	Long memory;
}
