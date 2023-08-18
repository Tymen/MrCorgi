package dev.tymen.MrCorgi.configurations;

import dev.tymen.MrCorgi.slashcommands.SlashCommandsHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Collections;

@Configuration
public class JDAConfig {

    @Bean
    public JDA jda(SlashCommandsHandler slashCommandsHandler) throws LoginException {
        JDA jda = JDABuilder.createLight(System.getProperty("discord_token"), Collections.emptyList())
                .setActivity(Activity.playing("Type /ping"))
                .build();
        jda.addEventListener(slashCommandsHandler);
        return jda;
    }
}

