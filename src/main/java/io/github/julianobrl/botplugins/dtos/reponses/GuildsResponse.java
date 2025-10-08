package io.github.julianobrl.botplugins.dtos.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildsResponse {
    private String name;
    private String iconUrl;
    private String bannerUrl;
    private String id;

    public static GuildsResponse fromGuild(Guild guild){

        return GuildsResponse.builder()
            .id(guild.getId())
            .name(guild.getName())
            .iconUrl(guild.getIconUrl())
            .bannerUrl(guild.getBannerUrl())
        .build();
    }
}
