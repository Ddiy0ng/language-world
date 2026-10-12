package com.languageworld.be.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserService {

    // 나이대 계산
    private int calculateAgeGroup(LocalDate birth) {

        LocalDate now = LocalDate.now();

        int age = Period.between(birth, LocalDate.now()).getYears();
        int ageGroup = (age / 10) * 10;

        return ageGroup;
    }
}
