package com.sfn.securityservice.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfn.securityservice.entities.AppRole;
import com.sfn.securityservice.entities.AppUser;
import com.sfn.securityservice.entities.RoleUserForm;
import com.sfn.securityservice.service.AccountServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountRestContoller {
    @Autowired
    AccountServiceImpl accountService;

    @GetMapping(path = "/users")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser> appUsers(){
       return accountService.listUsers();
    }

    @PostMapping(path = "/users")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "/roles")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    @PostAuthorize("hasAuthority('ADMIN')")
    public void addRoleToUser(@RequestBody RoleUserForm roleUserForm){
        accountService.addRoleToUSer(roleUserForm.getUsername(), roleUserForm.getRoleName());
    }
    @GetMapping(path = "/refreshToken")
    public void refreshtoken(HttpServletRequest request, HttpServletResponse response) throws Exception{
       String authToken = request.getHeader("Authorization");
       if(authToken != null && authToken.startsWith("Bearer ")){
           try{
               String refreshToken = authToken.substring(7);
               Algorithm algorithm = Algorithm.HMAC256("mySecret1234");
               JWTVerifier jwtVerifier = JWT.require(algorithm).build();
               DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
               String username = decodedJWT.getSubject();
               AppUser user = accountService.loadUserByUsername(username);
               //virifier la black list
               String jwtAccessToken = JWT.create()
                       .withSubject(user.getUsername())
                       .withExpiresAt(new Date(System.currentTimeMillis()+5*6*1000))
                       .withIssuer(request.getRequestURL().toString())
                       .withClaim("roles" ,user.getAppRoles().stream().map(ga->ga.getRoleName()).collect(Collectors.toList()))
                       .sign(algorithm);
               Map<String,String> idToken = new HashMap<>();
               idToken.put("access-token",jwtAccessToken);
               idToken.put("refresh-token",refreshToken);
               new ObjectMapper().writeValue(response.getOutputStream(),idToken);
               response.setContentType("application/json");
           }catch (Exception e){
               response.setHeader("error-message",e.getMessage());
               response.sendError(HttpServletResponse.SC_FORBIDDEN);
           }
       }else{
           new RuntimeException("Refresh token required !!");
       }
    }

}
