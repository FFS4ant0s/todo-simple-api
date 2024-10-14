package com.fernandosantos.todosimple.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorHashBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = "senha_hash"; // Substitua pela senha que deseja codificar
        String hash = encoder.encode(senha);
        System.out.println("Hash da senha: " + hash);
    }
}
