package io.oferto.poc_docker_client.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ContainerDto {
	String containerName;
	String imageName;
	String imageVersion;
	List<ContainerPortDto> publishPorts = new ArrayList<>();
	Boolean withAutoRemove = false;
	ContainerResourcesDto containerResourcesDto;
}
