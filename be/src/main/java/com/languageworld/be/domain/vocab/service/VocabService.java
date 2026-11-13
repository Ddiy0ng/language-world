package com.languageworld.be.domain.vocab.service;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.domain.vocab.dto.response.VocabCursorResponseDto;
import com.languageworld.be.domain.vocab.dto.response.VocabResponseDto;
import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import com.languageworld.be.domain.vocab.repository.VocabMeaningRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VocabService {

    private final VocabMeaningRepository vocabMeaningRepository;
    private final UserUtil userUtil;
    private final VocabMeaningUtil vocabMeaningUtil;

    @Transactional
    public VocabCursorResponseDto searchVocab(CustomPrincipal customPrincipal, String learningLanguage, String keyword, Long cursor) {

        UUID userUuid = customPrincipal.uuid();
        User user = userUtil.getUserByUuid(userUuid);
        String motherTongue = user.getMotherTongue().getType();

        Slice<VocabMeaning> vocabMeaningSlice = findVocabMeaning(learningLanguage, keyword, cursor);

        List<VocabResponseDto> vocabResponseDtoList = vocabMeaningSlice.getContent().stream()
                .map(vocabMeaning -> VocabResponseDto.from(
                        vocabMeaning,
                        vocabMeaningUtil.getMotherTongueVocabMeaningByVocabIdAndMotherTongueType(vocabMeaning.getVocab().getId(), motherTongue).getMeaning()
                ))
                .toList();

        boolean hasNext = vocabMeaningSlice.hasNext();

        Long nextCursor = null;
        if(hasNext && !vocabMeaningSlice.isEmpty())
            nextCursor = vocabMeaningSlice.getContent().get(vocabMeaningSlice.getNumberOfElements() - 1).getId();


        return VocabCursorResponseDto.from(nextCursor, hasNext, vocabResponseDtoList);
    }

    private Slice<VocabMeaning> findVocabMeaning(String learningLanguageType, String keyword, Long cursor) {

        int size = 100;
        Pageable pageable = PageRequest.of(0, size);
        Slice<VocabMeaning> vocabMeaningSlice;

        vocabMeaningSlice = vocabMeaningRepository.findSlice(learningLanguageType, keyword, pageable, cursor);

        return vocabMeaningSlice;
    }
}
