# 第一章：OAuth2基础知识和概念
OAuth2 是一种授权框架，用于为第三方应用程序提供安全的、受保护的访问资源的方法。在 OAuth2 中，有一些基本的概念需要了解：

* 资源服务器(Resource Server)：提供受保护的资源的服务器。
* 授权服务器(Authorization Server)：颁发访问令牌并对访问令牌进行验证的服务器。
* 客户端(Client)：请求访问受保护资源的应用程序。
* 用户(End User)：拥有受保护资源的用户。
## OAuth2 定义了四种授权方式：

* 授权码模式(Authorization Code)：通过授权服务器颁发一个授权码，客户端通过该授权码向授权服务器申请访问令牌。
* 简化模式(Implicit)：直接向授权服务器申请访问令牌，适用于一些无法保护授权码的环境，比如 JavaScript 应用。
* 密码模式(Resource Owner Password Credentials)：用户将用户名和密码提供给客户端，客户端使用这些凭据向授权服务器申请访问令牌。
* 客户端模式(Client Credentials)：客户端使用自己的凭据向授权服务器申请访问令牌，适用于客户端本身需要访问资源的情况。

# Spring Security OAuth2的使用
Spring Security OAuth2提供了一组基本的类和接口，可以很容易地实现 OAuth2 的授权和认证流程。
下面是使用 Spring Security OAuth2 的一般步骤：

* 添加依赖：在项目中添加 Spring Security OAuth2 的依赖，可以通过 Maven 或 Gradle 等方式进行添加。
```
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>2.4.0</version>
</dependency>
```
* 配置客户端信息
  在授权服务器中配置客户端信息，包括客户端 ID、客户端密码、授权类型、回调地址等信息。可以使用内存存储方式或数据库存储方式存储客户端信息。
* 配置scurity
```

/**
 * SpringSecurity配置 使用了WebSecurityConfigurerAdapter来配置安全设置
 * Created by loukaikai on 2020/6/19.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 这里的configure()方法配置了安全策略，
     * 规定了哪些URL是不需要进行安全认证的，哪些需要进行认证。这里的配置允许端点请求，
     * 允许RSA公钥请求，允许Swagger的API文档请求，允许OAuth2的令牌请求，
     * 并且要求其他所有请求都必须进行身份认证。
     * **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers("/rsa/publicKey").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/oauth/token").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * 用于获取 AuthenticationManager 实例
     * **/
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * PasswordEncoder用于加密和解密用户密码。
     * 在这个配置类中，passwordEncoder()方法被@Bean注解标记，
     * 以便可以创建PasswordEncoder实例并在其他地方使用
     * **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
```

* 配置授权服务器：
  在应用程序中配置授权服务器，包括 TokenStore、AuthorizationServerTokenServices、ClientDetailsService、AuthorizationEndpoint、TokenEndpoint 等组件。

```/**
 * 认证服务器配置
 * loukaikai on 2023/02/24 on 2020/6/19.
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
   //   private final UserServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenEnhancer jwtTokenEnhancer;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("admin-app")
                .secret(passwordEncoder.encode("123456"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600*24)
                .refreshTokenValiditySeconds(3600*24*7)
                .and()
                .withClient("portal-app")
                .secret(passwordEncoder.encode("123456"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600*24)
                .refreshTokenValiditySeconds(3600*24*7);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
        endpoints.authenticationManager(authenticationManager)
              //  .userDetailsService(userDetailsService) //配置加载用户信息的服务
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(enhancerChain);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }

}
```
* 配置资源服务器：
  在应用程序中配置资源服务器，包括 ResourceServerTokenServices、ResourceServerConfigurer 等组件。

* 实现授权和认证
  在应用程序中实现 OAuth2 的授权和认证流程，包括用户登录、授权码获取、Access Token 获取、资源访问等步骤。

* 测试应用程序
  使用客户端工具（如 Postman）对应用程序进行测试，验证 OAuth2 的授权和认证流程是否正确。

以上是使用 Spring Security OAuth2 的一般步骤。在实际应用中，可以根据需要进行适当的调整和扩展，以满足具体的业务需求。

# Spring security oauth2 源码梳理
## Spring Security OAuth2提供了一些基本的类来实现OAuth2流程。下面是这些类的简单介绍：

* AuthorizationServerConfigurer：用于配置授权服务器。
* AuthorizationServerConfigurerAdapter：AuthorizationServerConfigurer接口的适配器类，用于快速配置授权服务器。
* ResourceServerConfigurer：用于配置资源服务器。
* ResourceServerConfigurerAdapter：ResourceServerConfigurer接口的适配器类，用于快速配置资源服务器。
* ClientDetailsService：用于管理客户端信息。
* UserDetailsService：用于管理用户信息。
* AuthenticationManager：用于进行身份认证。
* TokenStore：用于存储令牌信息。
* TokenEnhancer：用于对令牌进行增强。
* JwtAccessTokenConverter：用于将令牌转换为JWT格式。
* DefaultTokenServices：用于管理令牌信息的默认实现。

在上面的类中，AuthorizationServerConfigurerAdapter和ResourceServerConfigurerAdapter是适配器类，
它们提供了一些默认的实现，可以通过继承这些类来快速实现授权服务器和资源服务器。

## 除了这些类之外，Spring Security OAuth2还提供了一些注解和过滤器类，用于实现OAuth2流程。
* @EnableAuthorizationServer：用于开启授权服务器。
* @EnableResourceServer：用于开启资源服务器。
* OAuth2AuthenticationFilter：用于处理OAuth2认证流程的过滤器。
* OAuth2AuthenticationProcessingFilter：用于处理OAuth2认证流程的过滤器。  
  当我们发送一个请求到认证服务器的 /oauth/token 接口时，Spring Security OAuth2 中的 OAuth2AuthenticationProcessingFilter 就会接收到这个请求
  该过滤器的 doFilter() 方法会将请求传递给它的父过滤器 AbstractAuthenticationProcessingFilter 的 attemptAuthentication() 方法。
* OAuth2AccessTokenEndpoint：用于处理获取访问令牌的请求的控制器。
* OAuth2RefreshTokenEndpoint：用于处理刷新访问令牌的请求的控制器。
* OAuth2TokenEndpointFilter：用于处理获取和刷新访问令牌请求的过滤器。
