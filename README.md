# security1

1. WebMvcConfig 설정 <br>
ㄴ ViewResolver 설정(안해도 무방) <br><br>

2. SecurityConfig 설정<br>
ㄴ @Configuration, @EnabledWebSecurity 설정(시큐리티 필터가 스프링 필터체인에 등록됨)<br>
ㄴ WebSecurityConfigurerAdapter 상속<br>
  ㄴ configure(HttpSecurity http) Override 하여 http세팅<br><br>

3. Repository 설정<br>
ㄴ JpaResitory 상속 받아 UserRepository 설정<br><br>

4. SecurityConfig에서 BCryptPasswordEncoder를 @Bean으로 설정(해당 메서드의 리턴되는 오브젝트를 IoC로 등록)<br><br>

5. UserDtails를 상속받아 PrincipalDetails 생성<br>
ㄴ 오브젝트타입 => Authentication 타입객체<br>
ㄴ Authentication 안에 User 정보가 있어야 됨<br>
ㄴ User 오브젝트 타입 => UserDetails 타입 객체<br>
=> Security Session => Authentication => UserDetails(PrincipalDetails)<br><br>

6. UserDetailsService를 상속받아 PrincipalDetailsService를 생성 @Service 설정<br>
ㄴ loadUserByUsername을 @Override 받아 작성<br>
=> 시큐리티 설정에서 loginProcessingUrl<br>
=> /login 요청이 오면 자동으로 UserDetailsService 타입으로 Ioc 되어 있는 loadUserByUsername 함수가 실행<br>
ㄴ @Autowired 해서 UserRepository 설정<br>
ㄴ User userEntity = findByUsernane 세팅,  UserRepository에 public User findByUsername을 설정<br><br>
=> 시큐리티 session(내부 Authentication(내부 UserDetails))

7. SecurityConfig에 @EnableGlobalMethodSecurity(SecureEnabled=true)를 설정해줌<br><br>

8. OAuth2 Client 를 MvnRepository 에서 받음<br><br>

9. SecurityConfig에 .oauth2Login을 추가해줌<br><br>

10. SecurityConfig에 .loginPage("/loginForm") 을 추가해주고 구글 로그인이 완료된 뒤의 후처리를 함<br><br>

11. PrincipalOauth2UserService를 DeaultOAuth2UserService에서 상속받은 후 loadUser(OAuth2UserRequest userRequest)를 @Override<br>
ㄴ 구글 로그인버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth - Client 라이브러리) -> AccessToken 요청<br>
ㄴ userRequest정보 -> 회원프로필 받아야함(loadUser함수) ->  구글로부터 회원 프로필  받아줌
ㄴ OAuth2User oauth2User = super.loadUser(user

12. IndexController에서 public @ResponseBody String testLogin(Authentication authentication)을 생성해서 세션정보를 확인함 authentication.getPricipal()<br>
ㄴ PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPricipal();<br>
ㄴ PrincipalDetails 에서 @Data 를 설정하고 IndexController에서 principalDetails.getUser()를 통해 세션정보를 확인함 <br><br>

13. public @ResponseBody String testLogin(Authentication authentication)을 <br>
-> public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails)로 변경<br>
-> userDetails.getUser()으로 세션정보를 확인할 수 있음<br><br>

14. public @ResponseBody String testOAuthLogin(Authentication authentication)을 만들어줌 <br>
ㄴ OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal(); <br>
ㄴ System.out.println(oauth2User.getAttributes()); 로 확인가능<br><br>
ㄴ 시큐리티 세션 내 Authentication객체안에 UserDetails(일반로그인), OAuth2User(OAuth로그인) 만 가능함 <br>
  ㄴ public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails userDetails) <== 일반로그인<br>
  ㄴ public @ResponseBody String user(@AuthenticationPrincipal OAuth2User userDetails) <== OAuth 로그인<br>
