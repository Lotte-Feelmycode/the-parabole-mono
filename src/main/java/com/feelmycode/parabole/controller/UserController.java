package com.feelmycode.parabole.controller;

import com.feelmycode.parabole.domain.User;
import com.feelmycode.parabole.dto.UserSigninDto;
import com.feelmycode.parabole.dto.UserSignupDto;
import com.feelmycode.parabole.global.api.ParaboleResponse;
import com.feelmycode.parabole.global.error.exception.ParaboleException;
import com.feelmycode.parabole.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<ParaboleResponse> signup(@RequestBody UserSignupDto dto) {

        User newUser = userService.signup(dto);
        if (dto.getRole() == 1) {
            return ParaboleResponse.CommonResponse(HttpStatus.CREATED,
                true, "사용자: 회원가입 성공");
        } else {
            return ParaboleResponse.CommonResponse(HttpStatus.CREATED, true,
                "판매자: 사용자 정보만 회원가입 성공. "
                    + "프론트에서 판매자 회원가입 과정을 이어서 작성하기 위해 반환받은 userId와 "
                    + "userRegisterDto를 POST /api/v1/seller 로 전송해주세요. ", newUser.getId());
            // TODO: 302 로 REST API로 Redirect 시킬 수 있는 방법 있는지 알아보기
        }
    }

    @GetMapping()
    public ResponseEntity<ParaboleResponse> signin(@RequestBody UserSigninDto dto) {

        if (!userService.signin(dto)) {
            throw new ParaboleException(HttpStatus.BAD_REQUEST, "로그인을 다시 시도하세요.");
        }
        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "로그인 성공");
    }

    @GetMapping("/role")
    public ResponseEntity<ParaboleResponse> checkAccountRole(@RequestParam String email) {
        // TODO: Role을 enum타입을 사용할 수 있게 변경
        if (userService.checkAccountRole(email) == 1) {
            return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "계정은 Role 은 사용자(USER) 입니다.", "USER");
        }
        return ParaboleResponse.CommonResponse(HttpStatus.OK, true, "계정은 Role 은 판매자(SELLER) 입니다.", "SELLER");
    }

}
