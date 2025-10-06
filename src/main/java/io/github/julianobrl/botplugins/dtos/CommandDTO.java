package io.github.julianobrl.botplugins.dtos;

import lombok.Data;

@Data
public class CommandDTO {
    private String command;
    private Object data;
}
