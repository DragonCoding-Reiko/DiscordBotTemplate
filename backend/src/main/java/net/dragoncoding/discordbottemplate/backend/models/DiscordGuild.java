package net.dragoncoding.discordbottemplate.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class DiscordGuild {
    @Id
    private Long guildId;

    @Setter
    private boolean deleted;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DiscordGuildProperty> properties;

    public void setProperty(String key, String value) {
        if (StringUtils.hasText(key)
            || StringUtils.hasText(value)) {
            return;
        }

        var existingProperty = properties.stream().filter(prop -> key.equals(prop.getKey())).findFirst();
        if (existingProperty.isPresent()) {
            existingProperty.get().setValue(value);
        } else {
            DiscordGuildProperty property = new DiscordGuildProperty(null, getGuildId(), key, value);
            properties.add(property);
        }
    }
}
