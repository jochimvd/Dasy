package xyz.vandijck.safety.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Autowired
    void configureObjectMapper(final ObjectMapper mapper) {
        mapper.registerModule(new Hibernate5Module());
        mapper.registerModule(new JavaTimeModule());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Set the task executor to synchronous, so we can test async actions synchronously in a test environment.
     * Sadly it's not possible to put a bean in a test class.
     *
     * @return The task executor.
     */
    @Profile("test")
    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
