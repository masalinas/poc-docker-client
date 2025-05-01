package io.oferto.poc_docker_client.dto;

import lombok.Data;

@Data
public class ContainerPortDto {
	Integer hostPort;
	Integer containerPort;
}
