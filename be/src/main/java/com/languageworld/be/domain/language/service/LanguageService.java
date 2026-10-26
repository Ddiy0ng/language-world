package com.languageworld.be.domain.language.service;

import com.languageworld.be.domain.language.dto.LanguageAndLevelResponseDto;
import com.languageworld.be.domain.language.dto.LanguageResponseDto;
import com.languageworld.be.domain.language.dto.LevelResponseDto;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.repository.LanguageRepository;
import com.languageworld.be.domain.language.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LevelRepository levelRepository;

    public LanguageAndLevelResponseDto findAllLanguageAndLevel() {

        List<Language> languageList = languageRepository.findAll();
        List<Level> levelList = levelRepository.findAll();

        List<LanguageResponseDto> languageResponseDtoList = languageList.stream()
                .map(LanguageResponseDto::from)
                .toList();
        List<LevelResponseDto> levelResponseDtoList = levelList.stream()
                .map(LevelResponseDto::from)
                .toList();

        return LanguageAndLevelResponseDto.of(languageResponseDtoList, levelResponseDtoList);
    }
}
