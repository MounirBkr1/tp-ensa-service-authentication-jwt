package com.ensa.tp_authentication_ms.security;



import javax.servlet.Filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


//pour chaque requete envoyé par user, cette methode va s'executée d'abord
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    //pr chaque requete envoyé par user,cette methode va s'executer d abord
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /*
         quand le server backend essaie de se connecter, le serveur bachend envoie une requete au backend
         en demandant les OPTIONS/tasks
         le serveur backend renvois les  options: Access-Control-Allow-Origin ....
           -Access-Control-Allow-Origin: je t autorise en tant que navigateur web de m'envoyer des requetes d'une page que tu as recuperer de n'importe quel doamin.
           -Access-Control-Allow-Headers: je t autorise de m'envoyer ces entetes: Origin...,Authorization.
            -Access-Control-Expose-Headers:  qd j'envoie ds requette http une entete(like:authorization),
                j'autorise l'application a lire contenu de cette entete
            -Access-Control-Allow-Methods :spécifie une ou plusieurs méthodes autorisées lors de l'accès à une ressource en réponse à une demande de contrôle en amont.
         */

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,authorization");
        response.addHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, authorization");
        response.addHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,PATCH");

        //si une requete est envoyé avec option => je l'autorise ,pas la peine de chercher JWT
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        //si qlq1cherche de s'autentifier,je l'autorise ,pas la peine de chercher JWT
//        else if(request.getRequestURI().equals("/login"))
//        {
//            //si c'est page /login ,je passe
//            filterChain.doFilter(request, response);
//            return;
//        }
        else {
            //System.out.println(new Date(System.currentTimeMillis()));
            //System.out.println(new Date(System.currentTimeMillis() + SecurityParams.EXPIRATION));

            //recuperer jwt
            String jwtToken=request.getHeader(SecurityParams.JWT_HEADER_NAME);

            if(jwtToken==null || !jwtToken.startsWith(SecurityParams.HEADER_PREFIX)){
                //ne rien faire ==return
                filterChain.doFilter(request,response);
                return;
            }

            //verifier la signature de l algorithm
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();

            //decoder en enlevant "Bearer "avec espace,il reste token
            String jwt = jwtToken.substring(SecurityParams.HEADER_PREFIX.length());
            DecodedJWT decodedJWT = verifier.verify(jwt);
            //System.out.println("JWT=" + jwt);

            //recuperer les  informations que contient le token:subject+roles+...
            String username = decodedJWT.getSubject();
            List<String> roles=decodedJWT.getClaims().get("roles").asList(String.class);
            //System.out.println("username=" + username);
            //System.out.println("roles=" + roles);

            Collection<GrantedAuthority> authorities=new ArrayList<>();


            roles.forEach(rn->{
                authorities.add(new SimpleGrantedAuthority(rn));
            });

            //authentifier l user qui est porté par jwt, mot de passe null
            UsernamePasswordAuthenticationToken user=
                    new UsernamePasswordAuthenticationToken(username,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(user);

            //passer au filtre suivant
            filterChain.doFilter(request,response);

        }
    }
}
