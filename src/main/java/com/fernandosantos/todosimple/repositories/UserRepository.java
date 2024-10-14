package com.fernandosantos.todosimple.repositories; // Verifique se o pacote está correto

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fernandosantos.todosimple.models.User; // Certifique-se de que o modelo User está corretamente importado

@Repository // Anotação que indica que esta é uma classe de repositório
public interface UserRepository extends JpaRepository<User, Long> {

    // Método para buscar um usuário pelo nome de usuário
    @Transactional(readOnly = true) // Indica que este método é somente leitura
    User findByUsername(String username);

}