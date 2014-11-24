package com.sulaco.fuse.example.tools;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SimpleStringReverser implements StringReverser {

    @Override
    public Optional<String> reverse(Optional<String> input) {

        if (input.isPresent()) {
            return Optional.of(
                    new StringBuilder(input.get())
                            .reverse()
                            .toString()
            );
        }

        return Optional.empty();
    }
}
