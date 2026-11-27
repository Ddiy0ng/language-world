package com.languageworld.be.domain.game.controller;

import com.languageworld.be.domain.game.dto.request.GameroomCreateRequestDto;
import com.languageworld.be.domain.game.dto.request.GameroomEnterRequestDto;
import com.languageworld.be.domain.game.dto.request.GameKickVoteRequestDto;
import com.languageworld.be.domain.game.dto.request.GameKickVoteStartRequestDto;
import com.languageworld.be.domain.game.dto.response.GameroomCreateResponseDto;
import com.languageworld.be.domain.game.dto.response.GameroomEnterResponseDto;
import com.languageworld.be.domain.game.dto.response.GameroomResponseDto;
import com.languageworld.be.domain.game.service.GameKickVoteService;
import com.languageworld.be.domain.game.service.GameroomService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gamerooms")
public class GameroomController {

    private final GameroomService gameroomService;
    private final GameKickVoteService gameKickVoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<GameroomCreateResponseDto>> createGameroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                 @RequestBody @Valid GameroomCreateRequestDto gameroomCreateRequestDto) {

        GameroomCreateResponseDto gameroomCreateResponseDto = gameroomService.createGameroom(customPrincipal, gameroomCreateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.GAMEROOM_CREATE_SUCCESS, gameroomCreateResponseDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GameroomResponseDto>>> findGameroomList() {

        List<GameroomResponseDto> gameroomResponseDtoList = gameroomService.searchGameroomList();

        return ResponseEntityUtil.success(SuccessCode.GAMEROOM_LIST_FIND_SUCCESS, gameroomResponseDtoList);
    }

    @PostMapping("/{gameroomId}")
    public ResponseEntity<ApiResponse<GameroomEnterResponseDto>> enterGameroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                               @PathVariable Long gameroomId,
                                                                               @RequestBody(required = false) @Valid GameroomEnterRequestDto gameroomEnterRequestDto) {

        GameroomEnterResponseDto gameroomEnterResponseDto = gameroomService.enterGameroom(customPrincipal, gameroomId, gameroomEnterRequestDto);

        return ResponseEntityUtil.success(SuccessCode.GAMEROOM_ENTER_SUCCESS, gameroomEnterResponseDto);
    }

    @DeleteMapping("/{gameroomId}")
    public ResponseEntity<ApiResponse<Void>> quitGameroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                          @PathVariable Long gameroomId) {

        gameroomService.quitGameroom(customPrincipal, gameroomId);

        return ResponseEntityUtil.success(SuccessCode.GAMEROOM_QUIT_SUCCESS);
    }

    // 방장: 추방 투표 시작
    @PostMapping("/{gameroomId}/kick-vote")
    public ResponseEntity<ApiResponse<Void>> startKickVote(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                           @PathVariable Long gameroomId,
                                                           @RequestBody @Valid GameKickVoteStartRequestDto gameKickVoteStartRequestDto) {

        gameKickVoteService.startKickVote(customPrincipal, gameroomId, gameKickVoteStartRequestDto);

        return ResponseEntityUtil.success(SuccessCode.KICK_VOTE_START_SUCCESS);
    }

    // 참가자(대상 제외): 찬반 투표
    @PostMapping("/{gameroomId}/kick-vote/ballot")
    public ResponseEntity<ApiResponse<Void>> kickVote(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                      @PathVariable Long gameroomId,
                                                      @RequestBody @Valid GameKickVoteRequestDto gameKickVoteRequestDto) {

        gameKickVoteService.kickVote(customPrincipal, gameroomId, gameKickVoteRequestDto);

        return ResponseEntityUtil.success(SuccessCode.KICK_VOTE_SUCCESS);
    }
}