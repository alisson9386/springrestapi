package api.rest.services;

import api.rest.model.Usuario;
import api.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*Consulta no banco o usuario*/
        Usuario usuario = usuarioRepository.findUserByLogin(username);
        if(username == null){
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        return new User(usuario.getLogin(),
                        usuario.getPassword(),
                        usuario.getAuthorities());
    }
}
