package com.univ.rankingplanet.login;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_info")
public class SiteUser {

//    @Id
//    @Column(name = "user_id") // 중복처리
//    private String userId;
//
//    @Column(name = "name")
//    private String name;
//
//    @Column(name = "password")
//    private String passWord;
//
////    @Column(name = "phone_num")
////    private String phoneNum;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;
}
