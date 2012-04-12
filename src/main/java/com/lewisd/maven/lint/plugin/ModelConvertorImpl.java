package com.lewisd.maven.lint.plugin;

import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugin.internal.DefaultLegacySupport;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.DefaultMavenProjectBuilder;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuilderConfiguration;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.properties.internal.EnvironmentUtils;

public class ModelConvertorImpl implements ModelConvertor {

    private final LegacySupport legacySupport;

	private final MavenProjectBuilder mavenProjectBuilder;

    public ModelConvertorImpl() {
    	legacySupport = new DefaultLegacySupport();
    	mavenProjectBuilder = new DefaultMavenProjectBuilder();
    }

	public MavenProject convertProject(MavenProject oldProject) {
		try {
			Field projectBuilderConfigurationField = oldProject.getClass().getDeclaredField("projectBuilderConfiguration");
			projectBuilderConfigurationField.setAccessible(true);
			Object object = projectBuilderConfigurationField.get(oldProject);
			if (object instanceof ProjectBuilderConfiguration) {
				// maven 2, need to re-parse to get InputLocations
				System.err.println("Reparsing model for maven2");
				final ProjectBuilderConfiguration projectBuilderConfiguration = (ProjectBuilderConfiguration) object;
				final ProjectBuildingRequest projectBuildingRequest = injectSession(toRequest(projectBuilderConfiguration));
				
			} else {
				System.err.println("Not reparsing, on maven3");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		/*
		File file = oldMavenProject.getFile();
		
		DefaultModelReader defaultModelReader = new DefaultModelReader();
		Map<String, ?> options = new HashMap<String, Object>();
		options.put(ModelReader.INPUT_SOURCE, '');
		Model newModel = defaultModelReader.read(file, options);
		*/
		
		return oldProject;
	}


	// Copied from DefaultMavenProjectBuilder
	private ProjectBuildingRequest toRequest( ProjectBuilderConfiguration configuration )
    {
        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();

        request.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MAVEN_2_0 );
        request.setResolveDependencies( false );

        request.setLocalRepository( configuration.getLocalRepository() );
        request.setBuildStartTime( configuration.getBuildStartTime() );
        request.setUserProperties( configuration.getUserProperties() );
        request.setSystemProperties( configuration.getExecutionProperties() );

        ProfileManager profileManager = configuration.getGlobalProfileManager();
        if ( profileManager != null )
        {
            request.setActiveProfileIds( profileManager.getExplicitlyActivatedIds() );
            request.setInactiveProfileIds( profileManager.getExplicitlyDeactivatedIds() );
        }
        else
        {
            /*
             * MNG-4900: Hack to workaround deficiency of legacy API which makes it impossible for plugins to access the
             * global profile manager which is required to build a POM like a CLI invocation does. Failure to consider
             * the activated profiles can cause repo declarations to be lost which in turn will result in artifact
             * resolution failures, in particular when using the enhanced local repo which guards access to local files
             * based on the configured remote repos.
             */
            MavenSession session = legacySupport.getSession();
            if ( session != null )
            {
                MavenExecutionRequest req = session.getRequest();
                if ( req != null )
                {
                    request.setActiveProfileIds( req.getActiveProfiles() );
                    request.setInactiveProfileIds( req.getInactiveProfiles() );
                }
            }
        }

        return request;
    }

	// Copied from DefaultMavenProjectBuilder
    private ProjectBuildingRequest injectSession( ProjectBuildingRequest request )
    {
        MavenSession session = legacySupport.getSession();
        if ( session != null )
        {
            request.setRepositorySession( session.getRepositorySession() );
            request.setSystemProperties( session.getSystemProperties() );
            if ( request.getUserProperties().isEmpty() )
            {
                request.setUserProperties( session.getUserProperties() );
            }

            MavenExecutionRequest req = session.getRequest();
            if ( req != null )
            {
                request.setRemoteRepositories( req.getRemoteRepositories() );
            }
        }
        else
        {
            Properties props = new Properties();
            EnvironmentUtils.addEnvVars( props );
            props.putAll( System.getProperties() );
            request.setSystemProperties( props );
        }

        return request;
    }

}
