package com.capstone2024.gym_management_system.infrastructure.configuration.oauth.google;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.entities.LoginType;
import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.account.enums.LoginMethod;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication.JwtService;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.AccountRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.LoginTypeRepository;
import com.capstone2024.gym_management_system.infrastructure.repositories.account.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final AccountRepository accountRepository;
    private final LoginTypeRepository loginTypeRepository;
    private final ProfileRepository profileRepository;
    private final JwtService jwtService;

    public CustomOAuth2UserService(AccountRepository accountRepository, LoginTypeRepository loginTypeRepository, ProfileRepository profileRepository, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.loginTypeRepository = loginTypeRepository;
        this.profileRepository = profileRepository;
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Loading user from OAuth2 provider with request: {}", userRequest);

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        logger.info("Received user with email: {} and name: {}", email, name);

        if (email == null || !email.endsWith("@fpt.edu.vn")) {
            logger.error("Invalid email domain: {}", email);
            throw new OAuth2AuthenticationException("Only FPT accounts are allowed.");
        }

        Account account = accountRepository.findAccountByEmail(email).orElse(null);

        if (Objects.isNull(account)) {
            logger.info("Account not found for email: {}. Creating a new account.", email);

            Account newAccountWithGoogle = Account.builder()
                    .email(email)
                    .role(Role.ADMINISTRATOR)
                    .status(Status.ACTIVE)
                    .build();

            account = accountRepository.save(newAccountWithGoogle);

            LoginType adminLoginType = LoginType.builder()
                    .method(LoginMethod.GOOGLE)
                    .account(newAccountWithGoogle)
                    .build();

            loginTypeRepository.save(adminLoginType);

            // Create and save Profile for the newAccountWithGoogle account
            Profile newProfile = Profile.builder()
                    .account(newAccountWithGoogle)
                    .fullName(name)
                    .phoneNumber(null)
                    .address(null)
                    .dateOfBirth(null)
                    .build();

            profileRepository.save(newProfile);

            logger.info("New account and profile created for email: {}", email);
        } else {
            logger.info("Existing account found for email: {}", email);
        }
//        attributes.put("account", account);

        return new DefaultOAuth2User(
                account.getAuthorities(),
                attributes,
                "email"
        );
    }
}
