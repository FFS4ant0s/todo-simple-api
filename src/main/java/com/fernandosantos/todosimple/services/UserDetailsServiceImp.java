package com.fernandosantos.todosimple.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fernandosantos.todosimple.Security.UserSpringSecurity;
import com.fernandosantos.todosimple.models.User;
import com.fernandosantos.todosimple.models.enums.ProfileEnum;
import com.fernandosantos.todosimple.repositories.UserRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        Set<ProfileEnum> profiles = user.getProfiles(); // Certifique-se de que isso é do tipo Set<ProfileEnum>
        UserSpringSecurity userSpringSecurity = new UserSpringSecurity(user.getId(), user.getUsername(),
                user.getPassword(), profiles);

        return userSpringSecurity;
    }
}
