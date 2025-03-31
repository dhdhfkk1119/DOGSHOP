package com.mysite.jjw.SecurityConfig;

import com.mysite.jjw.UserRole.SignRole;
import com.mysite.jjw.entity.Sign;
import com.mysite.jjw.repository.SignUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SignSecurityService implements UserDetailsService {
    private final SignUserRepository signUserRepository;

    // 사용자가 존재하지 않을 경우 예외 처리
    public class CustomUserNotFoundException extends AuthenticationException {
        public CustomUserNotFoundException(String msg) {

            super(msg);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws CustomUserNotFoundException {
        Optional<Sign> _sign = this.signUserRepository.findById(id);

        if(_sign.isEmpty()){
            throw new CustomUserNotFoundException("사용자를 찾을수 없습니다.");
        }

        Sign sign = _sign.get();

        // 1. 사용자의 권한(ROLE) 설정
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if("admin".equals(id)){
            authorityList.add(new SimpleGrantedAuthority(SignRole.ADMIN.getValue()));
        }else {
            authorityList.add(new SimpleGrantedAuthority(SignRole.SIGN.getValue()));
        }
        // 2. UserDetails를 구현한 User 객체 반환 (Spring Security에서 제공하는 기본 User 사용)
        return new User(sign.getId(),sign.getPassword(),authorityList);
    }
}
