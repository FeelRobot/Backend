package com.feelrobot.feelrobot.service.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feelrobot.feelrobot.config.JwtTokenProvider;
import com.feelrobot.feelrobot.dto.sign.*;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.*;
import com.feelrobot.feelrobot.repository.*;
import com.nimbusds.jose.JWSObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;
    private final JavaMailSender javaMailSender;
    private static final String sender = "taehun8765@gmail.com";
    private final CertificationRepository certificationRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;

    @Value("${kakao.login.key}")
    private String clientId;

    @Value("${kakao.login.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.login.secret}")
    private String secret;

    public static int createNumber() {
        return (int) (Math.random() * 1000000);
    }

    @Override
    public void register(RegisterDto registerDto) throws RegisterDuplicationException, ResponseException {
        log.info("[SignServiceImpl] 회원가입 요청");

        try {
            if(registerDto.getRole() == 0){
                boolean isExistUser = studentRepository.existsById(registerDto.getId());
                boolean isExistEmail = studentRepository.existsByEmail(registerDto.getEmail());

                if(isExistUser){
                    throw new RegisterDuplicationException("이미 존재하는 아이디입니다.", 400);
                }
                if(isExistEmail){
                    throw new RegisterDuplicationException("이미 존재하는 이메일입니다.", 400);
                }
                Student student = Student.builder()
                        .studentId(registerDto.getId())
                        .email(registerDto.getEmail())
                        .password(passwordEncoder.encode(registerDto.getPassword()))
                        .name(registerDto.getName())
                        .build();
                studentRepository.save(student);
            }
            if(registerDto.getRole() == 1){
                boolean isExistUser = parentRepository.existsById(registerDto.getId());
                boolean isExistEmail = parentRepository.existsByEmail(registerDto.getEmail());

                if(isExistUser){
                    throw new RegisterDuplicationException("이미 존재하는 아이디입니다.", 400);
                }
                if(isExistEmail){
                    throw new RegisterDuplicationException("이미 존재하는 이메일입니다.", 400);
                }
                Parent parent = Parent.builder()
                        .parentId(registerDto.getId())
                        .email(registerDto.getEmail())
                        .password(passwordEncoder.encode(registerDto.getPassword()))
                        .name(registerDto.getName())
                        .build();
                parentRepository.save(parent);
            }
        } catch (Exception e) {
            log.error("[SignServiceImpl] 회원가입 실패");
            throw new ResponseException("회원가입에 실패했습니다.", 500);
        }
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws ResponseException {
        log.info("[SignServiceImpl] 로그인 요청");
        if(studentRepository.findById(loginRequestDto.getId()).isPresent()){
            Student student = studentRepository.findById(loginRequestDto.getId())
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(!passwordEncoder.matches(loginRequestDto.getPassword(), student.getPassword())){
                throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
            }
            String accessToken = jwtTokenProvider.createAccessToken(student.getStudentId());
            String refreshToken = jwtTokenProvider.createRefreshToken();

            Refresh refresh = Refresh.builder()
                    .userId(student.getStudentId())
                    .token(refreshToken)
                    .build();
            refreshRepository.save(refresh);

            return LoginResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else if(parentRepository.findById(loginRequestDto.getId()).isPresent()){
            Parent parent = parentRepository.findById(loginRequestDto.getId())
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(!passwordEncoder.matches(loginRequestDto.getPassword(), parent.getPassword())){
                throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
            }
            String accessToken = jwtTokenProvider.createAccessToken(parent.getParentId());
            String refreshToken = jwtTokenProvider.createRefreshToken();
            Refresh refresh = Refresh.builder()
                    .userId(parent.getParentId())
                    .token(refreshToken)
                    .build();
            refreshRepository.save(refresh);
            return LoginResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new ResponseException("존재하지 않는 아이디입니다.", 400);
        }
    }

    @Override
    public void logout(String refreshToken) throws ResponseException {
        log.info("[SignServiceImpl] 로그아웃 요청");

        if(!jwtTokenProvider.validationToken(refreshToken)){
            throw new ResponseException("유효하지 않은 토큰입니다.", 400);
        }
        Refresh refresh = refreshRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseException("존재하지 않는 토큰입니다.", 400));
        refreshRepository.deleteByToken(refreshToken);
    }

    @Override
    public String refreshToken(RefreshDto refreshDto) throws ResponseException {
        log.info("[SignServiceImpl] 토큰 재발급 요청");

        Refresh refresh = refreshRepository.findByToken(refreshDto.getRefreshToken())
                .orElseThrow(() -> new ResponseException("존재하지 않는 토큰입니다.", 400));

        if(!jwtTokenProvider.validationToken(refreshDto.getRefreshToken())){
            throw new ResponseException("유효하지 않은 토큰입니다.", 400);
        }

        return jwtTokenProvider.createAccessToken(refresh.getUserId());
    }
////
    @Override
    public LoginResponseDto kakaoGetToken(String code) throws ResponseException {
        log.info("[SignServiceImpl] 카카오 토큰 요청");

        String url = "https://kauth.kakao.com/oauth/token";

        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build();

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", clientId);
            formData.add("redirect_uri", redirectUri);
            formData.add("client_secret", secret);
            formData.add("code", code);

            Mono<String> response = webClient.post()
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class);

            KakaoResponseDto kakaoResponseDto = new ObjectMapper().readValue(response.block(), KakaoResponseDto.class);
            String idToken = kakaoResponseDto.getId_token();

            String email = extractEmailFromToken(idToken);

            if(studentRepository.existsByEmail(email)){
                String accessToken = jwtTokenProvider.createAccessToken(email);
                String refreshToken = jwtTokenProvider.createRefreshToken();


                Refresh refresh = Refresh.builder()
                        .userId(email)
                        .token(refreshToken)
                        .build();
                refreshRepository.save(refresh);

                return LoginResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else if(parentRepository.existsByEmail(email)){
                String accessToken = jwtTokenProvider.createAccessToken(email);
                String refreshToken = jwtTokenProvider.createRefreshToken();

                Refresh refresh = Refresh.builder()
                        .userId(email)
                        .token(refreshToken)
                        .build();
                refreshRepository.save(refresh);

                return LoginResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

            } else {
                return LoginResponseDto.builder()
                        .accessToken(email)
                        .refreshToken("none")
                        .build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseException("카카오 토큰 요청에 실패했습니다.", 500);
        }
    }


    @Override
    public void checkId(String id) throws ResponseException {
        log.info("[SignServiceImpl] 아이디 중복 확인 요청");

        boolean isExistUser = studentRepository.existsById(id) || parentRepository.existsById(id);
        if(isExistUser){
            throw new ResponseException("이미 존재하는 아이디입니다.", 400);
        }
    }

    @Override
    public SimpleMailMessage createMail(String email, int number) throws ResponseException {
        log.info("[SignServiceImpl] 이메일 생성 요청");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(sender);
            message.setSubject("인증번호 발송");
            createNumber();
            message.setText("인증번호는 " + number + "입니다.");
            return message;
        } catch (RuntimeException e) {
            log.error("[SignServiceImpl] 이메일 생성 실패");
            throw new ResponseException("이메일 생성에 실패했습니다.", 500);
        }
    }

    @Override
    public int sendMail(MailDto mail) throws ResponseException {
        log.info("[SignServiceImpl] 메일 전송 요청");
        try {
            int number = createNumber();
            log.info("[SignServiceImpl] mail = {}", mail.getEmail());
            SimpleMailMessage message = createMail(mail.getEmail(), number);
            javaMailSender.send(message);

            Certification certification = Certification.builder()
                    .certificationEmail(mail.getEmail())
                    .certificationNumber(number)
                    .build();

            certificationRepository.save(certification);
            return number;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseException("메일 전송에 실패했습니다.", 500);
        }
    }

    @Override
    public boolean checkEmail(String email, int number) throws ResponseException {
        List<Certification> certificationList = certificationRepository.findAllByCertificationEmail(email);
        Certification certification = certificationList.get(certificationList.size() - 1);
        try {
            int code = certification.getCertificationNumber();
            if(code == number){
                return true;
            }
            throw new ResponseException("인증번호가 일치하지 않습니다.", 400);
        } catch (Exception e) {
            throw new ResponseException("인증번호 확인 중 문제가 발생했습니다.", 500);
        }
    }

    private String extractEmailFromToken(String idToken) throws ResponseException {
        try{
            JWSObject jwsObject = JWSObject.parse(idToken);
            Map<String, Object> claims = jwsObject.getPayload().toJSONObject();

            return (String) claims.get("email");
        } catch (ParseException e){
            log.error("[SignServiceImpl] 토큰 파싱 실패");
            throw new ResponseException("토큰 파싱에 실패했습니다.", 500);
        }
    }

    @Override
    public boolean existId(String email) throws ResponseException {
        log.info("[SignServiceImpl] 아이디 존재 여부 확인 요청");

        if(studentRepository.findByEmail(email).isPresent()){
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 이메일입니다.", 400));
            sendMail(new MailDto(email));
            return true;
        } else if(parentRepository.findByEmail(email).isPresent()){
            Parent parent = parentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 이메일입니다.", 400));
            sendMail(new MailDto(email));
            return true;
        } else{
            throw new ResponseException("존재하지 않는 이메일입니다.", 400);
        }
    }

    @Override
    public String findId(String email) throws ResponseException {
        log.info("[SignServiceImpl] 아이디 찾기 요청");

        if(studentRepository.findByEmail(email).isPresent()){
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 이메일입니다.", 400));
            return student.getStudentId();
        } else if(parentRepository.findByEmail(email).isPresent()){
            Parent parent = parentRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 이메일입니다.", 400));
            return parent.getParentId();
        } else{
            throw new ResponseException("존재하지 않는 이메일입니다.", 400);
        }
    }

    @Override
    public boolean isCorrectId(String id, String email) throws ResponseException {
        log.info("[SignServiceImpl] 아이디 확인 요청");

        if(studentRepository.findById(id).isPresent()){
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(student.getEmail().equals(email)){
                sendMail(new MailDto(email));
                return true;
            } else {
                throw new ResponseException("아이디와 이메일이 일치하지 않습니다.", 400);
            }
        } else if(parentRepository.findById(id).isPresent()){
            Parent parent = parentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(parent.getEmail().equals(email)){
                sendMail(new MailDto(email));
                return true;
            } else {
                throw new ResponseException("아이디와 이메일이 일치하지 않습니다.", 400);
            }
        } else {
            throw new ResponseException("존재하지 않는 아이디입니다.", 400);
        }
    }

    @Override
    public void updatePassword(String id, String password) throws ResponseException {
        log.info("[SignServiceImpl] 비밀번호 변경 요청");
        try {
            if (studentRepository.findById(id).isPresent()) {
                Student student = studentRepository.findById(id)
                        .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
                student.setPassword(passwordEncoder.encode(password));
                studentRepository.save(student);
            } else if (parentRepository.findById(id).isPresent()) {
                Parent parent = parentRepository.findById(id)
                        .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
                parent.setPassword(passwordEncoder.encode(password));
                parentRepository.save(parent);
            } else {
                throw new ResponseException("존재하지 않는 아이디입니다.", 400);
            }
        } catch (Exception e) {
            log.error("[SignServiceImpl] 비밀번호 변경 실패");
            throw new ResponseException("비밀번호 변경에 실패했습니다.", 500);
        }
    }
}
