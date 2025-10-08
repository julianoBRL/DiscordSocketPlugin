package io.github.julianobrl.botplugins.dtos.requests;

import lombok.Data;

@Data
public class CommandRequest {
    private String command;
    private Object data;
}
