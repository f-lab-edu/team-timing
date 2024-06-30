package com.github.thundermarket.thundermarket.domain;

import java.io.Serializable;
import java.util.Objects;

public class SessionUser implements Serializable {

    private String email;

    public SessionUser() {}

    private SessionUser(Builder builder) {
        this.email = builder.email;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String email;

        public Builder() {}

        public Builder(SessionUser sessionUser) {
            this.email = sessionUser.email;
        }

        public Builder userEmail(String userEmail) {
            this.email = userEmail;
            return this;
        }

        public SessionUser build() {
            return new SessionUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionUser that = (SessionUser) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public String toString() {
        return "SessionUser{" +
                "email='" + email + '\'' +
                '}';
    }
}
