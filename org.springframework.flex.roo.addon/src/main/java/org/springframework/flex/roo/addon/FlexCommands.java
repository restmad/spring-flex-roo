package org.springframework.flex.roo.addon;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.roo.support.util.Assert;

/**
 * Sample of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colours on 
 * non-Windows systems).
 * 
 */
@Component
@Service
public class FlexCommands implements CommandMarker {
	
	private static Logger logger = HandlerUtils.getLogger(FlexCommands.class);

	@Reference private FlexOperations operations;
	@Reference private MetadataService metadataService;
	
	@CliAvailabilityIndicator({"flex setup", "flex remoting scaffold", "flex remoting all"})
	public boolean isFlexAvailable() {
		return operations.isFlexAvailable();
	}
	
	@CliCommand(value="flex setup", help="Install Spring BlazeDS configuration artifacts into your project")
	public void installFlex() {
		operations.installFlex();
	}
	
	@CliCommand(value="flex remoting scaffold", help="Create a new scaffold Service (ie with full CRUD operations) exposed as a Flex Remoting Destination")
	public void newRemotingDestination(
			@CliOption(key={"name",""}, mandatory=true, help="The path and name of the service object to be created") JavaType service,
			@CliOption(key="entity", mandatory=false, optionContext="update,project", unspecifiedDefaultValue="*", help="The name of the entity object which the service exposes to the flex client") JavaType entity) {
		operations.createRemotingDestination(service, entity);
	}
	
	@CliCommand(value="flex remoting all", help="Scaffold a Service for all entities without an existing Remoting Destination")
	public void generateAll(@CliOption(key="package", mandatory=true, help="The package in which new services will be placed") JavaPackage javaPackage) {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Could not obtain ProjectMetadata");
		if (!javaPackage.getFullyQualifiedPackageName().startsWith(projectMetadata.getTopLevelPackage().getFullyQualifiedPackageName())) {
			logger.warning("Your service was created outside of the project's top level package and is therefore not included in the preconfigured component scanning. Please adjust your component scanning manually in applicationContext.xml");
		}
		operations.generateAll(javaPackage);
	}
	
}