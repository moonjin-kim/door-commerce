package com.loopers.domain.user;

public class UserCommand {
    public record Create(
            String name,
            String account,
            String email,
            String birthday,
            Gender gender
    ) {
    }
}
