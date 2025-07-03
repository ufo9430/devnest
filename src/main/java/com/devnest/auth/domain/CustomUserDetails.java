package com.devnest.auth.domain;

import com.devnest.user.domain.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String email;
  private final String password;
  private final String nickname;
  private final String profileImage;
  private final boolean enabled;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user) {
    this.userId = user.getUserId();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.nickname = user.getNickname();
    this.profileImage = user.getProfileImage();
    this.enabled = user.getIsActive();
    this.authorities = List.of(user.getRole());
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
