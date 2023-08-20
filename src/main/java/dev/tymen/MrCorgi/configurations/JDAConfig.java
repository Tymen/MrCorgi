package dev.tymen.MrCorgi.configurations;

import dev.tymen.MrCorgi.slashcommands.SlashCommandsHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Collections;

@Configuration
public class JDAConfig {
    @Value("${discord.token}")
    private String token;

    @Bean
    public JDA jda(SlashCommandsHandler slashCommandsHandler) throws LoginException {
        if (token == null || token.equals("")) {
            token = System.getProperty("discord_token");
        }

        JDA jda = JDABuilder.createLight(token, Collections.emptyList())
                .setActivity(Activity.playing("Type /upcoming"))
                .build();
        jda.addEventListener(slashCommandsHandler);
        return jda;
    }
}

