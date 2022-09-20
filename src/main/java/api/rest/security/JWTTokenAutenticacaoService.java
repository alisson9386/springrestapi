package api.rest.security;

import api.rest.app.ApplicationContextLoad;
import api.rest.model.Usuario;
import api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class JWTTokenAutenticacaoService {

    //Tempo de validade do token
    private static final long EXPIRATION_TIME = 172800000;

    //Senha unica para compor a autenticação
    private static final String SECRET = "zaq12wsxZAQ!@WSX";

    //Prefixo padrão do token
    private static final String TOKEN_PREFIX = "Bearer";

    private static final String HEADER_STRING = "Authorization";

    /*Gerando token de autenticação e adicionando ao Header*/
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {

        /*Mostragem do token*/
        String JWT = Jwts.builder() //Chama o gerador de token
                .setSubject(username) //Adiciona o usuario
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Tempo de expiração
                .signWith(SignatureAlgorithm.HS512, SECRET).compact(); //Compactação e algoritimos de geração de senha

        String token = TOKEN_PREFIX + " " + JWT; //Bearer 156189189189

        //Adiciona no Header
        response.addHeader(HEADER_STRING, token);

        //Escreve token no corpo do http
        response.getWriter().write("{\"Authorization\": \""+token+"\"}");
    }


    /*Retorna o usuario validado com token, ou retorna null caso nao seja valido*/
    public Authentication getAuthentication(HttpServletRequest request){
        //Pega o token enviado no cabeçalho
        String token = request.getHeader(HEADER_STRING);
        if(token != null){
            //Faz a validação do token
            String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody().getSubject();

            if(user != null){

                Usuario usuario = ApplicationContextLoad.getApplicationContext()
                        .getBean(UsuarioRepository.class).findUserByLogin(user);

                //Retorna usuario logado
                if(usuario != null){
                    return new UsernamePasswordAuthenticationToken(
                            usuario.getLogin(),
                            usuario.getSenha(),
                            usuario.getAuthorities());
                }
            }
        }
        return null;
    }

}
