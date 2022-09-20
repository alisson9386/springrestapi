package api.rest.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import api.rest.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("SELECT U FROM Usuario U WHERE U.login = ?1")
	Usuario findUserByLogin(String login);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE usuario SET token = ?1 WHERE login = ?2")
	void atualizaTokenUser(String token, String login);

}
