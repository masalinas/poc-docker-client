package io.oferto.poc_docker_client.dto;

import lombok.Data;

@Data
public class ContainerResourcesDto {
	Long cpuCount;
	Long cpuPeriod;
	Long cpuQuota;
	Long memory;
}
