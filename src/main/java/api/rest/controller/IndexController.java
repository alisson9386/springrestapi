package api.rest.controller;

import java.util.List;

import api.rest.model.UsuarioDTO;
import api.rest.services.UsuarioService;
import api.rest.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private UsuarioService usuarioService;
	@Autowired
	private VendaService vendaService;
	
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id, @PathVariable(value = "venda") Long venda) {
		return usuarioService.relatorio(id, venda);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	@CacheEvict(value = "cacheUser1", allEntries = true)
	@CachePut("cacheUser1")
	public ResponseEntity<UsuarioDTO> init1(@PathVariable(value = "id") Long id) {
		return usuarioService.init1(id);
	}

	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
	@CacheEvict(value = "cacheUser2", allEntries = true)
	@CachePut("cacheUser2")
	public ResponseEntity<UsuarioDTO> init2(@PathVariable(value = "id") Long id) {
		return usuarioService.init2(id);
	}

	//Implementando cache simulando a busca de varios usuarios no banco
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheUsers", allEntries = true)
	@CachePut("cacheUsers")
	public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException {
		return usuarioService.usuarios();
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) throws Exception {
		return usuarioService.cadastrarUsuario(usuario);
	}
	
	
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
		return vendaService.cadastrarVenda(iduser, idvenda);
	}
	
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable(value = "id") Long id) throws Exception {
		return usuarioService.atualizarUsuario(usuario, id);
	}
	
	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity atualizarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
		return vendaService.atualizarVenda(iduser, idvenda);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deleteUser (@PathVariable(value = "id") Long id) {
		return usuarioService.deleteUser(id);
	}
	
	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deleteVendas(@PathVariable(value = "id") Long id) {
		return vendaService.deleteVendas(id);
	}

}
