package huce.nguyentoan.job4u.service;

import huce.nguyentoan.job4u.domain.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        String email = user.getAttribute("email");

        // Nếu user chưa tồn tại, tạo mới
        User existingUser = userService.handleGetUserByUsername(email);
        if (existingUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(user.getAttribute("name"));
            newUser.setPassword("");
            userService.handleCreateUser(newUser);
        }
        return user;
    }
}
