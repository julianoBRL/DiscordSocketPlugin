package io.github.julianobrl.botplugins.dtos.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelfInfoResponse {
    private OffsetDateTime createdAt;
    private String name;
    private String avatarUrl;
    private boolean verified;
    private boolean bot;
    private boolean system;

    public static SelfInfoResponse fromSelfUser(SelfUser self){
        return SelfInfoResponse.builder()
            .createdAt(self.getTimeCreated())
            .name(self.getEffectiveName())
            .avatarUrl(self.getAvatarUrl())
            .verified(self.isVerified())
            .bot(self.isBot())
            .system(self.isSystem())
        .build();
    }
}
