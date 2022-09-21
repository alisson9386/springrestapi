package api.rest.services;

import api.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class VendaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
    public ResponseEntity cadastrarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
        //Usuario cadUser = usuarioRepository.save(usuario);
        return new ResponseEntity("Venda gravada", HttpStatus.OK);
    }

    @PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
    public ResponseEntity atualizarVenda(@PathVariable(value = "iduser") Long iduser, @PathVariable(value = "idvenda") Long idvenda){
        //Usuario cadUser = usuarioRepository.save(usuario);
        return new ResponseEntity("Venda atualizada", HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/venda", produces = "application/text")
    public String deleteVendas(@PathVariable(value = "id") Long id) {
        usuarioRepository.deleteById(id);
        return "ok";
    }
}
