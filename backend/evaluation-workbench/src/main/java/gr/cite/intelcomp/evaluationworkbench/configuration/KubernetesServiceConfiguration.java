package gr.cite.intelcomp.evaluationworkbench.configuration;

public class KubernetesServiceConfiguration {

	private String deploymentName;

	private String containerName;
	
	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
}


