package io.oferto.poc_docker_client.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContainerDto {
	@Schema(description = "Container name")
	String containerName;
	@Schema(description = "Image name")
	String imageName;
	@Schema(description = "Image version")
	String imageVersion;
	@Schema(description = "Publish Ports collection")
	List<ContainerPortDto> publishPorts = new ArrayList<>();
	@Schema(description = "Autoremove the container when finalize. (default not autoremove)")
	Boolean withAutoRemove = false;
	@Schema(description = "Container resources: CPU/Memory")
	ContainerResourcesDto containerResourcesDto;
}
