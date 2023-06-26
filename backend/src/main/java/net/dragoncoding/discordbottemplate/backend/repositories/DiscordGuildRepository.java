package net.dragoncoding.discordbottemplate.backend.repositories;

import net.dragoncoding.discordbottemplate.backend.models.DiscordGuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordGuildRepository extends JpaRepository<DiscordGuild, Long> { }
