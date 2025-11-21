package huce.nguyentoan.job4u.service;

import huce.nguyentoan.job4u.domain.Response.ResLoginDTO;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userService.handleGetUserByUsername(email);

        ResLoginDTO res = new ResLoginDTO();
        res.setUser(new ResLoginDTO.UserLogin(
                user.getId(), user.getEmail(), user.getName(), user.getRole()
        ));

        String accessToken = securityUtil.createAccessToken(email, res);
        String refreshToken = securityUtil.createRefreshToken(email, res);

        userService.updateUserToken(refreshToken, email);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect("http://localhost:3000/login-success?token=" + accessToken);
    }
}
