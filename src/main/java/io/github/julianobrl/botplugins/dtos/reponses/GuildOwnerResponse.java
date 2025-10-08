package io.github.julianobrl.botplugins.dtos.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildOwnerResponse {
    private String name;
    private String avatarUrl;
    private String nickname;
    private boolean owner;
    private boolean boosting;

    public static GuildOwnerResponse fromMember(Member member){
        if(member == null){
            return null;
        }
        return GuildOwnerResponse.builder()
                .name(member.getEffectiveName())
                .nickname(member.getNickname())
                .avatarUrl(member.getAvatarUrl())
                .owner(member.isOwner())
                .boosting(member.isBoosting())
                .build();
    }

}
