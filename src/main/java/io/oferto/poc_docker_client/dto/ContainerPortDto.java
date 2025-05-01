package io.oferto.poc_docker_client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContainerPortDto {
	@Schema(description = "Host port")
	Integer hostPort;
	@Schema(description = "Container port")
	Integer containerPort;
}
