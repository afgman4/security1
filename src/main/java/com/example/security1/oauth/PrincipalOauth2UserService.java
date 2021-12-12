package com.example.security1.oauth;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.oauth.provider.FacebookUserInfo;
import com.example.security1.oauth.provider.GoogleUserInfo;
import com.example.security1.oauth.provider.NaverUserInfo;
import com.example.security1.oauth.provider.OAuth2UserInfo;
import com.example.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    //private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리된 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest :  " + userRequest);
        System.out.println("getCleintRegistration : " + userRequest.getClientRegistration());
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글로그인 버튼 클릭 -> 구글로그인 창 -> 로그인 완료 -> code 리턴(oAuth-Client 라이브러리) -> Access 토큰
        // userRequest 정보 -> loadUser 함수 -> 회원프로필
        System.out.println("getAttributes : " + oAuth2User.getAttributes());

        System.out.println("userRequest.getClientRegistration() : " + userRequest.getClientRegistration());

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("구글과 페이스북만 가능");
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();
        //String password = bCryptPasswordEncoder.encode("겟인데어");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null){
            userEntity = User.builder()
                            .username(username)
                            .email(email)
                            .role(role)
                            .provider(provider)
                            .providerId(providerId)
                            .build();

            userRepository.save(userEntity);
        }else{
            System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동로그인이 됩니다.");
        }

        // 회원가입을 강제로 할 예정
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
