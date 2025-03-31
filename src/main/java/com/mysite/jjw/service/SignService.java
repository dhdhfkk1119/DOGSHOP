package com.mysite.jjw.service;

import com.mysite.jjw.DTO.SignDTO;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SignService {

    private final SignUserRepository signUserRepository;
    private final PasswordEncoder passwordEncoder;

    //중복확인 API
    public boolean isIdExists(String id) {
        return signUserRepository.existsById(id); // 중복 검사
    }

    // 회원가입 API
    public Sign registerSign(SignDTO signDTO){
        if(signUserRepository.existsById(signDTO.getId())){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }
        if(signUserRepository.existsByEmail(signDTO.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        String emailDomain = signDTO.getEmailDomain();
        if (emailDomain != null) {
            emailDomain = emailDomain.replaceAll("^,|,$", "").trim(); // 앞뒤 쉼표 제거
        }

        if (emailDomain == null || emailDomain.isEmpty()) {
            throw new IllegalArgumentException("이메일 도메인을 선택하거나 입력하세요.");
        }

        // DTO - > Entity 변환
        Sign sign = new Sign();
        sign.setId(signDTO.getId());
        sign.setName(signDTO.getName());
        sign.setSex(signDTO.getSex());
        sign.setPhone(signDTO.getPhone());
        sign.setMemberDate(LocalDate.now()); // memberDate 자동 설정
        sign.setPassword(passwordEncoder.encode(signDTO.getPassword())); // 비밀번호 암호화
        sign.setEmail(signDTO.getEmailLocal() + "@" + signDTO.getEmailDomain()); // 이메일 로컬과 도메인 결합
        sign.setAge(signDTO.getAge());  // LocalDate로 변환된 나이 설정

        return signUserRepository.save(sign);
    }

}
