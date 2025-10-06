package io.github.julianobrl.botplugins.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildDto {
    private String name;
    private String iconUrl;
    private String bannerUrl;
    private String vanityUrl;
    private String splashUrl;
    private String description;
    private GuildOwnerDto owner;
    private Guild.NSFWLevel nsfwLevel;
    private Guild.BoostTier boostTier;
    private int boostCount;
    private int maxEmojis;
    private long maxFileSize;
    private int memberCount;
    private int maxMembers;
    private boolean invitesDisabled;

    public static GuildDto fromGuild(Guild guild){
        return GuildDto.builder()
            .name(guild.getName())
            .iconUrl(guild.getIconUrl())
            .bannerUrl(guild.getBannerUrl())
            .vanityUrl(guild.getVanityUrl())
            .splashUrl(guild.getSplashUrl())
            .description(guild.getDescription())
            .owner(GuildOwnerDto.fromMember(guild.getOwner()))
            .nsfwLevel(guild.getNSFWLevel())
            .boostTier(guild.getBoostTier())
            .boostCount(guild.getBoostCount())
            .maxEmojis(guild.getMaxEmojis())
            .maxFileSize(guild.getMaxFileSize())
            .memberCount(guild.getMemberCount())
            .maxMembers(guild.getMaxMembers())
            .invitesDisabled(guild.isInvitesDisabled())
        .build();
    }
}
