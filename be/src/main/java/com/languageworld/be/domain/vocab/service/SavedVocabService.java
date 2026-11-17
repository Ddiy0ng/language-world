package com.languageworld.be.domain.vocab.service;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.domain.vocab.dto.request.SavedVocabDeleteRequestDto;
import com.languageworld.be.domain.vocab.dto.request.SavedVocabSaveRequestDto;
import com.languageworld.be.domain.vocab.dto.request.SavedVocabUpdateRequestDto;
import com.languageworld.be.domain.vocab.dto.response.SavedVocabCursorResponseDto;
import com.languageworld.be.domain.vocab.dto.response.SavedVocabResponseDto;
import com.languageworld.be.domain.vocab.entity.SavedVocab;
import com.languageworld.be.domain.vocab.entity.VocabMeaning;
import com.languageworld.be.domain.vocab.repository.SavedVocabRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedVocabService {

    private final SavedVocabRepository savedVocabRepository;
    private final UserUtil userUtil;
    private final VocabMeaningUtil vocabMeaningUtil;
    private final SavedVocabUtil savedVocabUtil;

    @Transactional
    public void saveSavedVocab(CustomPrincipal customPrincipal, SavedVocabSaveRequestDto savedVocabSaveRequestDto) {

        UUID userUuid = customPrincipal.uuid();
        User user = userUtil.getUserByUuid(userUuid);
        VocabMeaning vocabMeaning = vocabMeaningUtil.getVocabMeaningByVocabMeaningId(userUuid, savedVocabSaveRequestDto.vocabMeaningId());

        Optional<SavedVocab> optionalSavedVocab = savedVocabRepository.findByUser_UuidAndVocabMeaning_Id(userUuid, vocabMeaning.getId());
        if(optionalSavedVocab.isPresent())
            return;

        SavedVocab savedVocab = SavedVocab.of(savedVocabSaveRequestDto.memo(), user, vocabMeaning);
        savedVocabRepository.save(savedVocab);
    }

    @Transactional
    public SavedVocabCursorResponseDto searchSavedVocabList(CustomPrincipal customPrincipal, String learningLanguage, String keyword, Long cursor) {

        UUID userUuid = customPrincipal.uuid();
        User user = userUtil.getUserByUuid(userUuid);
        if(learningLanguage == null)
            learningLanguage = user.getLearningLanguage().getType();

        Slice<SavedVocab> savedVocabSlice = findSavedVocab(userUuid, learningLanguage, keyword, cursor);

        List<SavedVocabResponseDto> savedVocabResponseDtoList = savedVocabSlice.getContent().stream()
                .map(SavedVocabResponseDto::from)
                .toList();

        boolean hasNext = savedVocabSlice.hasNext();

        Long nextCursor = null;
        if(hasNext && !savedVocabSlice.isEmpty())
            nextCursor = savedVocabSlice.getContent().get(savedVocabSlice.getNumberOfElements() - 1).getId();


        return SavedVocabCursorResponseDto.from(nextCursor, hasNext, savedVocabResponseDtoList);
    }

    @Transactional
    public void changeSavedVocabMemo(CustomPrincipal customPrincipal, Long savedVocabId, SavedVocabUpdateRequestDto savedVocabUpdateRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        SavedVocab savedVocab = savedVocabUtil.getSavedVocabById(userUuid, savedVocabId);
        savedVocabUtil.savedVocabOwnerCheck(savedVocab, userUuid);

        savedVocab.changeMemo(savedVocabUpdateRequestDto.memo());
    }

    @Transactional
    public void removeSavedVocab(CustomPrincipal customPrincipal, Long savedVocabId) {

        UUID userUuid = customPrincipal.uuid();

        SavedVocab savedVocab = savedVocabUtil.getSavedVocabById(userUuid, savedVocabId);
        savedVocabUtil.savedVocabOwnerCheck(savedVocab, userUuid);

        savedVocabRepository.delete(savedVocab);
    }

    @Transactional
    public void removeSavedVocabList(CustomPrincipal customPrincipal, SavedVocabDeleteRequestDto savedVocabDeleteRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        List<Long> savedVocabIdList = savedVocabDeleteRequestDto.savedVocabIdList();

        savedVocabIdList.forEach(savedVocabId -> {
            SavedVocab savedVocab = savedVocabUtil.getSavedVocabById(userUuid, savedVocabId);
            savedVocabUtil.savedVocabOwnerCheck(savedVocab, userUuid);
            savedVocabRepository.delete(savedVocab);
        });
    }

    private Slice<SavedVocab> findSavedVocab(UUID userUuid, String language, String keyword, Long cursor) {

        int size = 100;
        Pageable pageable = PageRequest.of(0, size);
        Slice<SavedVocab> savedVocabSlice;

        savedVocabSlice = savedVocabRepository.findSlice(userUuid, language, keyword, pageable, cursor);

        return savedVocabSlice;
    }
}
