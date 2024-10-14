package com.fernandosantos.todosimple.Security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fernandosantos.todosimple.models.enums.ProfileEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserSpringSecurity(Long id, String username, String password, Set<ProfileEnum> profileEnums) {
        this.id = id;
        this.username = username;
        this.password = password;

        // Verifique se profileEnums não é nulo
        if (profileEnums == null) {
            profileEnums = Set.of(); // ou crie um novo conjunto vazio
        }

        // Mapeando corretamente as autoridades
        this.authorities = profileEnums.stream()
                .map(profileEnum -> {
                    // Aqui é onde você pode verificar o tipo de profileEnum
                    if (profileEnum instanceof ProfileEnum) {
                        return new SimpleGrantedAuthority(profileEnum.getDescription());
                    } else {
                        throw new IllegalArgumentException("Tipo inesperado: " + profileEnum.getClass());
                    }
                })
                .collect(Collectors.toList());
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
        return true;
    }

    public boolean hasRole(ProfileEnum profileEnum) {
        return authorities.contains(new SimpleGrantedAuthority(profileEnum.getDescription()));
    }
}
