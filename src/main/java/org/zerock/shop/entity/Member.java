package org.zerock.shop.entity;

import org.zerock.shop.constant.Role;
import org.zerock.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;

@Entity
@Table(name="member")
@Getter @Setter
@ToString
public class Member extends BaseEntity{
    // Auditing 기능 적용 BaseEntity 상속 받음

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)  // 유니크 처리
    private String email;  // 회원 검색 처리용

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;  // constant.Role 사용자, 관리자 구분용

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());  // 패스워드 암호화 처리
        member.setPassword(password);
        member.setRole(Role.ADMIN);  //권한은 ADMIN
        return member;
    } // 회원 생성용 메서드 (dto와 암호화를 받아 Member 객체 리턴)

}