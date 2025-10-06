package io.github.julianobrl.botplugins.singletons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

public class SingleObjectMapper {
    private static SingleObjectMapper instance;

    @Getter
    private final ObjectMapper mapper;

    public SingleObjectMapper() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    public static SingleObjectMapper getInstance(){
        if(instance == null)
            instance = new SingleObjectMapper();
        return instance;
    }

}
