package net.dragoncoding.discordbottemplate.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DiscordGuildProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long guildId;

    private String key;

    @Setter
    private String value;
}
