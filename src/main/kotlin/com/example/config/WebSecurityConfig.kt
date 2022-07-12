
package com.example.config


import com.example.security.AuthenticationFilter
import com.example.security.CustomAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private val authenticationFilter: AuthenticationFilter? = null

    @Autowired
    private val exceptionHandlerEntryPoint: CustomAuthenticationEntryPoint? = null

    override fun configure(webSecurity: WebSecurity) {
        webSecurity.ignoring().antMatchers(
            "/swagger-ui/",
            "/swagger-ui/{springfox,swagger-ui}.*",
            "/configuration/**",
            "/swagger-resources/**",
            "/v3/api-docs"
        )
    }

    override fun configure(httpSecurity: HttpSecurity) {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable() // dont authenticate this particular request
            .authorizeRequests()
            .antMatchers("/v1/health").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().formLogin().disable()
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling().authenticationEntryPoint(exceptionHandlerEntryPoint)
    }
}
