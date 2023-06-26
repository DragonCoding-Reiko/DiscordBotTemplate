package net.dragoncoding.discordbottemplate.app;

import net.dragoncoding.discordbottemplate.backend.DbConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DbConfig.class)
public class ApplicationConfig {
}
