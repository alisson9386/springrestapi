package api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import api.rest.model.UsuarioDTO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.rest.model.Telefone;
import api.rest.model.Usuario;
import api.rest.repository.TelefoneRepository;
import api.rest.repository.UsuarioRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id, @PathVariable(value = "venda") Long venda) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	@CacheEvict(value = "cacheUser1", allEntries = true)
	@CachePut("cacheUser1")
	public ResponseEntity<UsuarioDTO> init1(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Versão 1");
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
	@CacheEvict(value = "cacheUser2", allEntries = true)
	@CachePut("cacheUser2")
	public ResponseEntity<UsuarioDTO> init2(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Versão 2");
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}

	//Implementando cache simulando a busca de varios usuarios no banco
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheUsers", allEntries = true)
	@CachePut("cacheUsers")
	public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		//Thread.sleep(6000); //Segura o codigo por 6 segundos simulando um processo lento


		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
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
	
	
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
		//Usuario cadUser = usuarioRepository.save(usuario);
		return new ResponseEntity("Venda gravada", HttpStatus.OK);
	}
	
	@PutMapping(value = "/{id}", produces = "application/json")
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
	
	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity atualizarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
		//Usuario cadUser = usuarioRepository.save(usuario);
		return new ResponseEntity("Venda atualizada", HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deleteUser (@PathVariable(value = "id") Long id) {
		usuarioRepository.deleteById(id);
		return "ok";
	}
	
	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deleteVendas(@PathVariable(value = "id") Long id) {
		usuarioRepository.deleteById(id);
		return "ok";
	}

}
