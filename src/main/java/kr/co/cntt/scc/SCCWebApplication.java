package kr.co.cntt.scc;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

// Auto-configuration
// https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-auto-configuration.html
//
// Cache : https://spring.io/guides/gs/caching/
//
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableCaching
public class SCCWebApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

		return builder.sources(SCCWebApplication.class);

	}

	public static void main(String[] args) {
		SpringApplication.run(SCCWebApplication.class, args);
	}
	
	@Configuration
	@EnableAsync
	static class SpringAsyncConfig {

		public Executor threadPoolTaskExecutor() {
			return new ThreadPoolTaskExecutor();
		}
	}
	
	/**
	 * @author cntt
	 * TODO : 위치 변경???
	 */
	@Configuration
	static class WebMvcConfig extends WebMvcConfigurerAdapter {
		
		@Value("${filepath}") 
		String imgPath;
		
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			// file context
			//registry.addResourceHandler("/files/**").addResourceLocations("file:" + "C:\\Users\\cntt\\member\\images\\")
			//registry.addResourceHandler("/files/**").addResourceLocations("file:" + "root\\member\\images\\")
			registry.addResourceHandler("/files/**").addResourceLocations("file:" + imgPath)
				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
				.addResolver(new PathResourceResolver());
		}
	}
	

	// http://stackoverflow.com/a/24944671/3614964
	// https://github.com/wilkinsona/spring-boot-sample-tomcat-jndi/blob/master/src/main/java/sample/tomcat/jndi/SampleTomcatJndiApplication.java
	/*
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
					Tomcat tomcat) {

				tomcat.enableNaming();

				return super.getTomcatEmbeddedServletContainer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				// https://tomcat.apache.org/tomcat-8.0-doc/jndi-resources-howto.html
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/sccDataSource");
				resource.setType(DataSource.class.getName());
				resource.setProperty("driverClassName", "com.mysql.jdbc.Driver");
				resource.setProperty("url", "jdbc:mysql://127.0.0.1:3306/scc");
				resource.setProperty("username", "scc");
				resource.setProperty("password", "scc");

				context.getNamingResources().addResource(resource);
			}

		};

	}
	*/
}
