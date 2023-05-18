package com.example.app.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class MemberVO{
    private Long id;
    private String memberName;
    private String memeberEmail;
    private String memberPassword;
    private String memberRegisterDate;
    private char memberIsRemaining;
    private String memberBriefIntroduce;
    private String memberIntroduce;
    private String memberCategory;
    private String memberUrl;
}