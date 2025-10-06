package io.github.julianobrl.botplugins.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetActivityDto {
    private Activity.ActivityType type;
    private String name;
}
