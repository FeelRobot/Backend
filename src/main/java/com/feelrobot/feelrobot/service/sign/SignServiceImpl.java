package com.feelrobot.feelrobot.service.sign;

import com.feelrobot.feelrobot.config.JwtTokenProvider;
import com.feelrobot.feelrobot.dto.sign.LoginRequestDto;
import com.feelrobot.feelrobot.dto.sign.LoginResponseDto;
import com.feelrobot.feelrobot.dto.sign.RegisterDto;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Refresh;
import com.feelrobot.feelrobot.model.User;
import com.feelrobot.feelrobot.repository.RefreshRepository;
import com.feelrobot.feelrobot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;

    @Override
    public void register(RegisterDto registerDto) throws RegisterDuplicationException, ResponseException {
        log.info("[SignServiceImpl] 회원가입 요청");

        boolean isExistUser = userRepository.existsById(registerDto.getId());
        boolean isExistEmail = userRepository.existsByEmail(registerDto.getEmail());

        if(isExistUser){
            throw new RegisterDuplicationException("이미 존재하는 아이디입니다.", 400);
        }
        if(isExistEmail){
            throw new RegisterDuplicationException("이미 존재하는 이메일입니다.", 400);
        }
        try {
            User user = User.builder()
                    .id(registerDto.getId())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .name(registerDto.getName())
                    .role(registerDto.getRole())
                    .build();
            userRepository.save(user);
        } catch (Exception e) {
            log.error("[SignServiceImpl] 회원가입 실패");
            throw new ResponseException("회원가입에 실패했습니다.", 500);
        }
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws ResponseException {
        log.info("[SignServiceImpl] 로그인 요청");

        User user = userRepository.findById(loginRequestDto.getId())
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        Refresh refresh = Refresh.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build();

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
