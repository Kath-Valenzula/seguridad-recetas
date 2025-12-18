package com.demo.demo.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "UK_USUARIO_EMAIL", columnNames = "EMAIL"),
})
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String username;

  private String email;

  private String password;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  @com.fasterxml.jackson.annotation.JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Rol básico por defecto; extender si se manejan roles en BD
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  @com.fasterxml.jackson.annotation.JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @com.fasterxml.jackson.annotation.JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @com.fasterxml.jackson.annotation.JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @com.fasterxml.jackson.annotation.JsonIgnore
  public boolean isEnabled() {
    return true;
  }

}
