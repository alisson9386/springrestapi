package api.rest.services;

import api.rest.model.Usuario;
import api.rest.model.UsuarioDTO;
import api.rest.repository.UsuarioRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id, @PathVariable(value = "venda") Long venda) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
    }

    public ResponseEntity<UsuarioDTO> init1(@PathVariable(value = "id") Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        System.out.println("Versão 1");
        if(!usuario.isEmpty()){
            return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
        }
        return new ResponseEntity("Não foi possível encontrar usuário com o ID solicitado!", HttpStatus.OK);
    }

    public ResponseEntity<UsuarioDTO> init2(@PathVariable(value = "id") Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        System.out.println("Versão 2");
        if(!usuario.isEmpty()){
            return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
        }
        return new ResponseEntity("Não foi possível encontrar usuário com o ID solicitado!", HttpStatus.OK);
    }

    public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException {
        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
        //Thread.sleep(6000); //Segura o codigo por 6 segundos simulando um processo lento
        return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
    }

    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) throws Exception {
        for(int i = 0; i < usuario.getTelefones().size(); i++) {
            usuario.getTelefones().get(i).setUsuario(usuario);
        }

        //Consumindo API externa: viacep
        URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String cep = "";
        StringBuilder jsonCep = new StringBuilder();

        while ((cep = br.readLine()) != null){
            jsonCep.append(cep);
        }

        System.out.println(jsonCep.toString());

        Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

        usuario.setCep(userAux.getCep());
        usuario.setLogradouro(userAux.getLogradouro());
        usuario.setBairro(userAux.getBairro());
        usuario.setComplemento(userAux.getComplemento());
        usuario.setLocalidade(userAux.getLocalidade());
        usuario.setUf(userAux.getUf());

        String passwordCrypt = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(passwordCrypt);
        Usuario cadUser = usuarioRepository.save(usuario);
        return new ResponseEntity<Usuario>(cadUser, HttpStatus.OK);
    }

    public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable(value = "id") Long id) throws Exception {
        for(int i = 0; i < usuario.getTelefones().size(); i++) {
            usuario.getTelefones().get(i).setUsuario(usuario);
        }
        Usuario userTemp = usuarioRepository.findUserByLogin(usuario.getLogin());

        if(!userTemp.getSenha().equals(usuario.getSenha())){
            String passwordCrypt = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(passwordCrypt);
        }

        //Consumindo API externa: viacep
        URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String cep = "";
        StringBuilder jsonCep = new StringBuilder();

        while ((cep = br.readLine()) != null){
            jsonCep.append(cep);
        }

        System.out.println(jsonCep.toString());

        Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

        usuario.setCep(userAux.getCep());
        usuario.setLogradouro(userAux.getLogradouro());
        usuario.setBairro(userAux.getBairro());
        usuario.setComplemento(userAux.getComplemento());
        usuario.setLocalidade(userAux.getLocalidade());
        usuario.setUf(userAux.getUf());

        Usuario atualizarUser = usuarioRepository.save(usuario);
        return new ResponseEntity<Usuario>(atualizarUser, HttpStatus.OK);
    }

    public String deleteUser (@PathVariable(value = "id") Long id) {
        usuarioRepository.deleteById(id);
        return "ok";
    }
}
