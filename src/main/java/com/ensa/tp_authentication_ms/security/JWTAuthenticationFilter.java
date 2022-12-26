package com.ensa.tp_authentication_ms.security;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ensa.tp_authentication_ms.entities.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//JWTAuthenticationFilter :A servlet filter that extracts JSON Web Tokens from the Authorization request header and from the jwt query parameter for use as authentication tokens.
// UsernamePasswordAuthenticationFilter:Login forms must present two parameters to this filter: a username and password. The default parameter names to use are contained in the static fields
// SPRING_SECURITY_FORM_USERNAME_KEY and SPRING_SECURITY_FORM_PASSWORD_KEY. The parameter names can also be changed by setting the usernameParameter and passwordParameter properties.
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        //data has sent in json format:ObjectMapper=> deserialisation of object json on java object AppUser
        try {
            //recuperer les données saisie par l'user(json) et le deserialiser en java object
            AppUser appUser = new ObjectMapper().readValue(request.getInputStream(),AppUser.class);
            return  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(),appUser.getPassword()));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //getPrincipal: return user authentifié=objet user
        //getAuthority:return roles
        User user= (User) authResult.getPrincipal();

        List<String> roles= new ArrayList<>();
        authResult.getAuthorities().forEach(a->{
            roles.add(a.getAuthority());
        });

        //creer et signer token: header(codé en Base64URL) + payload + signature
        //signature : HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

        String jwt= JWT.create()
                //Issuer: l'autorité qui a creer le token
                .withIssuer(request.getRequestURI())
                //subject:nom de l user
                .withSubject(user.getUsername())
                //ArrayClaim: les roles,le convertir en tableau de string
                .withArrayClaim("roles",roles.toArray(new String[roles.size()]))
                //ExpirerAt: date d expiration en ms
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityParams.EXPIRATION))
                //algorithm de signature
                .sign(Algorithm.HMAC256(SecurityParams.SECRET));

        //ajouter jwt au header
        response.addHeader(SecurityParams.JWT_HEADER_NAME,jwt);
    }


}
