package uk.gov.cshr.atsadaptor.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorisationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Value("${security.oauth2.client.clientId}")
    private String client;
    @Value("${security.oauth2.refresh.token.duration:240}")
    private int refreshTokenDuration;
    @Value("${security.oauth2.client.clientSecret}")
    private String secret;
    @Value("${security.oauth2.token.duration:120}")
    private int tokenDuration;

    private AuthenticationManager authenticationManager;
    private TokenStore tokenStore;
    private UserApprovalHandler userApprovalHandler;

    /**
     * Single construction for autowiring
     *
     * @param authenticationManager instance of AuthenticationManager
     * @param tokenStore            instance of TokenStore
     * @param userApprovalHandler   instance of UserApprovalHandler
     */
    public AuthorisationServerConfiguration(AuthenticationManager authenticationManager, TokenStore tokenStore, UserApprovalHandler userApprovalHandler) {
        this.authenticationManager = authenticationManager;
        this.tokenStore = tokenStore;
        this.userApprovalHandler = userApprovalHandler;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(client)
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes("read", "write", "trust")
                .secret(secret)
                .accessTokenValiditySeconds(tokenDuration)
                .refreshTokenValiditySeconds(refreshTokenDuration);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler).authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.realm("CSR_VACANCIES_REALM/client");
    }
}
