package com.sonalake.meetup.utils;

import org.springframework.core.io.Resource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToString;

public class Utils {

    public static String resourceToString(Resource resource) {
        try {
            return copyToString(resource.getInputStream(), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
