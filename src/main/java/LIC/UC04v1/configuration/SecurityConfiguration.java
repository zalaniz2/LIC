package LIC.UC04v1.configuration;

import LIC.UC04v1.services.security.SecUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/*

 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider; //daoAuthenticationProvider

    @Autowired
    @Qualifier("daoAuthenticationProvider")
    //2nd
    //This refers to the daoAuthenticationProvider created
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    //1st
    //Spring firsts creates daoAuthenticationProvider (places it in spring context)
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, SecUserDetailsService userDetailsService){ //changed this to SecUserDetailService from UserDetailService
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Autowired
    //3rd
    //adding daoAuthenticationProvider to this authenticationManagerBuilder
    public void configureAuthManager(AuthenticationManagerBuilder authenticationManagerBuilder){
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable(); //WHAT DOES THIS DO??
        http.csrf().ignoringAntMatchers("h2-console").disable()
                /*.authorizeRequests().antMatchers("/login").permitAll()
                .and().authorizeRequests().antMatchers("/static/css").permitAll()
                .and().authorizeRequests().antMatchers("/js").permitAll()
                .and().formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/home")
                .and().authorizeRequests().antMatchers("/home").authenticated()
                .and().exceptionHandling().accessDeniedPage("/access_denied");*/




                .authorizeRequests()
                .antMatchers("/").permitAll()
                //.antMatchers("/export").permitAll()//Remove this????????
                .antMatchers("/login").permitAll()
                .antMatchers("/student/**").permitAll()
                .antMatchers("/doctor/**").permitAll()
                //.antMatchers("/registration").hasAuthority("ADMIN")
                //.and().authorizeRequests().antMatchers("/resources/**").permitAll()
                .antMatchers("/admin/adminView").hasAuthority("SUPER_ADMIN")
                //.antMatchers("/admin/registration").hasAuthority("SUPER_ADMIN")
                .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                //.antMatchers("/**").hasAuthority("ADMIN").anyRequest()
                //.antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                .authenticated().and().csrf().disable().formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
                .defaultSuccessUrl("/admin/home",true)
                .usernameParameter("username")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

                .logoutSuccessUrl("/")
                .and().exceptionHandling().accessDeniedPage("/access-denied");

    }


    //DO I NEED THIS???????
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**"); //allows access to these folders (will show login image even though not ADMIN)
    }


}