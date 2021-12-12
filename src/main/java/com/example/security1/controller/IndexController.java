package com.example.security1.controller;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/login ======================");

        System.out.println("authentication : " + authentication.getPrincipal());

        System.out.println("userDetails " + userDetails.getUsername());

        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/login ======================");

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        System.out.println("authentication : " + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());
//        System.out.println("authentication user " + principalDetails.getUser());
//        System.out.println("userDetails " + userDetails.getUsername());

        return "oAuth 세션 정보 확인하기";
    }

    @GetMapping({"","/"})
    public String Index(){
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String User(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String Admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String Manager(){
        return "manager";
    }

    @GetMapping("/loginForm")
    public String login(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String joinProc(){
        return "회원가입 완료";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "data";
    }
}
