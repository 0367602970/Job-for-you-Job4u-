package huce.nguyentoan.job4u.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.domain.Request.ReqLoginDTO;
import huce.nguyentoan.job4u.domain.Response.ResLoginDTO;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${nguyentoan.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //set thông tin người dùng đã đăng nhập vào Security Context (Có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create a token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
                    currentUserDB.getName());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
        res.setAccessToken(access_token);

        //Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

        //update user
        this.userService.updateUserToken(refresh_token, loginDto.getUsername());

        //set cookies
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();
        
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(res);
    }

    @GetMapping("/account")
    @ApiMessage("fecth account info")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token", defaultValue = "abc") String refreshToken) throws IdInvalidException {
        if (refreshToken .equals("abc")) {
            throw new IdInvalidException("Refresh token không có trong cookie");
        }

        //check valid token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        //check user by token and email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token không hợp lệ");
        }

        // issue new token /set refresh token as cookie
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
                    currentUserDB.getName());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(access_token);

        //Create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        //update user
        this.userService.updateUserToken(new_refresh_token, email);

        //set cookies
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", new_refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();
        
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(res);
    }
    
    @PostMapping("/logout")
    @ApiMessage("logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        //update refresh token = null
        this.userService.updateUserToken(null, email);

        //remove refresh token in cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
            .from("refresh_token", null)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }
    
}
