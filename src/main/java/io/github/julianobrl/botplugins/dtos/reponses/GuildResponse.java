package io.github.julianobrl.botplugins.dtos.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildResponse {
    private String id;
    private String name;
    private String iconUrl;
    private String bannerUrl;
    private String vanityUrl;
    private String splashUrl;
    private String description;
    private GuildOwnerResponse owner;
    private Guild.NSFWLevel nsfwLevel;
    private Guild.BoostTier boostTier;
    private int boostCount;
    private int maxEmojis;
    private long maxFileSize;
    private int memberCount;
    private int maxMembers;
    private boolean invitesDisabled;
    private DiscordLocale locale;

    public static GuildResponse fromGuild(Guild guild){
        return GuildResponse.builder()
            .id(guild.getId())
            .name(guild.getName())
            .iconUrl(guild.getIconUrl())
            .bannerUrl(guild.getBannerUrl())
            .vanityUrl(guild.getVanityUrl())
            .splashUrl(guild.getSplashUrl())
            .description(guild.getDescription())
            .owner(GuildOwnerResponse.fromMember(guild.getOwner()))
            .nsfwLevel(guild.getNSFWLevel())
            .boostTier(guild.getBoostTier())
            .boostCount(guild.getBoostCount())
            .maxEmojis(guild.getMaxEmojis())
            .maxFileSize(guild.getMaxFileSize())
            .memberCount(guild.getMemberCount())
            .maxMembers(guild.getMaxMembers())
            .invitesDisabled(guild.isInvitesDisabled())
            .locale(guild.getLocale())
        .build();
    }
}
