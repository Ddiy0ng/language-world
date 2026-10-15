import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';


// ======================================================
// 환경 설정
// ======================================================

class SecureConfig {
  static const String googleWebClientId =
  String.fromEnvironment(
    'GOOGLE_WEB_CLIENT_ID',
  );

  static const String kakaoNativeAppKey =
  String.fromEnvironment(
    'KAKAO_NATIVE_APP_KEY',
  );

  static const String springBaseUrl =
  String.fromEnvironment(
    'SPRING_BASE_URL',
  );
}


// Google Cloud Console의
// "Web application" OAuth Client ID
const String googleWebClientId =
    SecureConfig.googleWebClientId;


// Kakao Developers의
// "Native App Key"
const String kakaoNativeAppKey =
    SecureConfig.kakaoNativeAppKey;


// Android Emulator에서
// Windows PC의 Spring 서버에 접근하는 주소
const String springBaseUrl =
    SecureConfig.springBaseUrl;


// ======================================================
// 공통 로그 함수
// ======================================================

void logStep(
    String scope,
    String step,
    String message,
    ) {
  debugPrint(
    '[$scope][$step] $message',
  );
}


// ======================================================
// 앱 시작
// ======================================================

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();


  // ------------------------------------------------------
  // 환경변수 확인
  // 실제 값은 출력하지 않음
  // ------------------------------------------------------

  debugPrint(
    '[ENV] KAKAO_NATIVE_APP_KEY exists = '
        '${kakaoNativeAppKey.isNotEmpty}',
  );

  debugPrint(
    '[ENV] GOOGLE_WEB_CLIENT_ID exists = '
        '${googleWebClientId.isNotEmpty}',
  );

  debugPrint(
    '[ENV] SPRING_BASE_URL exists = '
        '${springBaseUrl.isNotEmpty}',
  );


  // 환경변수가 비어 있으면
  // 앱 시작 단계에서 즉시 오류 발생
  if (kakaoNativeAppKey.isEmpty) {
    throw StateError(
      'KAKAO_NATIVE_APP_KEY가 설정되지 않았습니다.',
    );
  }

  if (googleWebClientId.isEmpty) {
    throw StateError(
      'GOOGLE_WEB_CLIENT_ID가 설정되지 않았습니다.',
    );
  }

  if (springBaseUrl.isEmpty) {
    throw StateError(
      'SPRING_BASE_URL이 설정되지 않았습니다.',
    );
  }


  logStep(
    'APP',
    '1',
    'main() 시작',
  );


  // ------------------------------------------------------
  // Kakao SDK 초기화
  // ------------------------------------------------------

  logStep(
    'APP',
    '2',
    'KakaoSdk.init() 호출 직전',
  );

  await KakaoSdk.init(
    nativeAppKey: kakaoNativeAppKey,
  );

  logStep(
    'APP',
    '3',
    'KakaoSdk.init() 완료',
  );


  // ------------------------------------------------------
  // Google Sign-In 초기화
  // ------------------------------------------------------

  logStep(
    'APP',
    '4',
    'GoogleSignIn.initialize() 호출 직전',
  );

  await GoogleSignIn.instance.initialize(
    serverClientId: googleWebClientId,
  );

  logStep(
    'APP',
    '5',
    'GoogleSignIn.initialize() 완료',
  );


  // ------------------------------------------------------
  // Flutter 앱 시작
  // ------------------------------------------------------

  runApp(
    const MyApp(),
  );

  logStep(
    'APP',
    '6',
    'runApp() 호출 완료',
  );
}


// ======================================================
// Root Widget
// ======================================================

class MyApp extends StatelessWidget {
  const MyApp({
    super.key,
  });

  @override
  Widget build(
      BuildContext context,
      ) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SocialSignupPage(),
    );
  }
}


// ======================================================
// 소셜 회원가입 테스트 화면
// ======================================================

class SocialSignupPage extends StatefulWidget {
  const SocialSignupPage({
    super.key,
  });

  @override
  State<SocialSignupPage> createState() =>
      _SocialSignupPageState();
}


class _SocialSignupPageState
    extends State<SocialSignupPage> {

  String result =
      '아직 회원가입하지 않았습니다.';

  bool loading = false;


  // ====================================================
  // Google 회원가입
  // ====================================================

  Future<void> googleSignup() async {
    logStep(
      'GOOGLE',
      '1',
      'googleSignup() 시작',
    );

    try {
      setState(() {
        loading = true;
        result = 'Google 로그인 중...';
      });


      // ------------------------------------------------
      // Google 로그인 시작
      // ------------------------------------------------

      logStep(
        'GOOGLE',
        '2',
        'GoogleSignIn.authenticate() 호출 직전',
      );

      final GoogleSignInAccount account =
      await GoogleSignIn
          .instance
          .authenticate();

      logStep(
        'GOOGLE',
        '3',
        'GoogleSignIn.authenticate() 반환 성공',
      );


      // ------------------------------------------------
      // ID Token 획득
      // ------------------------------------------------

      logStep(
        'GOOGLE',
        '4',
        'ID Token 조회 직전',
      );

      final String? idToken =
          account
              .authentication
              .idToken;

      logStep(
        'GOOGLE',
        '5',
        'ID Token null 여부 = ${idToken == null}',
      );

      if (idToken == null) {
        throw Exception(
          'Google ID Token을 받지 못했습니다.',
        );
      }

      logStep(
        'GOOGLE',
        '6',
        'Google ID Token 획득 성공, '
            'length=${idToken.length}',
      );

      // 실제 토큰 문자열은 로그로 출력하지 않음


      // ------------------------------------------------
      // Spring 요청
      // ------------------------------------------------

      logStep(
        'GOOGLE',
        '7',
        'socialSignup(google) 호출 직전',
      );

      await socialSignup(
        socialType: 'google',
        token: idToken,
      );

      logStep(
        'GOOGLE',
        '8',
        'socialSignup(google) 정상 반환',
      );
    } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '================ GOOGLE ERROR ================',
    );

    debugPrint(
    '[GOOGLE][ERROR] '
    'type = ${e.runtimeType}',
    );

    debugPrint(
    '[GOOGLE][ERROR] '
    'error = $e',
    );

    debugPrint(
    '[GOOGLE][ERROR] '
    'stackTrace = $stackTrace',
    );

    debugPrint(
    '==============================================',
    );

    if (!mounted) {
    return;
    }

    setState(() {
    result =
    'Google 회원가입 실패\n\n'
    '${e.runtimeType}\n'
    '$e';
    });
    } finally {
    logStep(
    'GOOGLE',
    '9',
    'finally 진입',
    );

    if (mounted) {
    setState(() {
    loading = false;
    });
    }

    logStep(
    'GOOGLE',
    '10',
    'googleSignup() 종료',
    );
    }
  }


  // ====================================================
  // Kakao 회원가입
  // ====================================================

  Future<void> kakaoSignup() async {
    logStep(
      'KAKAO',
      '1',
      'kakaoSignup() 시작',
    );

    try {
      setState(() {
        loading = true;
        result = 'Kakao 로그인 중...';
      });


      // ------------------------------------------------
      // KakaoTalk 설치 여부 확인
      // ------------------------------------------------

      logStep(
        'KAKAO',
        '2',
        'isKakaoTalkInstalled() 호출 직전',
      );

      final bool installed =
      await isKakaoTalkInstalled();

      logStep(
        'KAKAO',
        '3',
        'KakaoTalk 설치 여부 = $installed',
      );


      OAuthToken oauthToken;


      // ------------------------------------------------
      // KakaoTalk 설치된 경우
      // ------------------------------------------------

      if (installed) {
        logStep(
          'KAKAO',
          '4-A',
          'loginWithKakaoTalk() 호출 직전',
        );

        try {
          oauthToken =
          await UserApi
              .instance
              .loginWithKakaoTalk();

          logStep(
            'KAKAO',
            '5-A',
            'loginWithKakaoTalk() 반환 성공',
          );
        } catch (
    e,
    stackTrace,
    ) {
    logStep(
    'KAKAO',
    'ERROR-A',
    'KakaoTalk 로그인 실패',
    );

    debugPrint(
    '[KAKAO][ERROR-A] '
    'type = ${e.runtimeType}',
    );

    debugPrint(
    '[KAKAO][ERROR-A] '
    'error = $e',
    );

    debugPrint(
    '[KAKAO][ERROR-A] '
    'stackTrace = $stackTrace',
    );


    // --------------------------------------------
    // KakaoTalk 실패
    // → KakaoAccount fallback
    // --------------------------------------------

    logStep(
    'KAKAO',
    '4-B',
    'loginWithKakaoAccount() '
    'fallback 호출 직전',
    );

    oauthToken =
    await UserApi
        .instance
        .loginWithKakaoAccount();

    logStep(
    'KAKAO',
    '5-B',
    'loginWithKakaoAccount() '
    'fallback 반환 성공',
    );
    }
    }

    // ------------------------------------------------
    // KakaoTalk 미설치
    // ------------------------------------------------

    else {
    logStep(
    'KAKAO',
    '4-C',
    'KakaoTalk 미설치 → '
    'loginWithKakaoAccount() 호출 직전',
    );

    oauthToken =
    await UserApi
        .instance
        .loginWithKakaoAccount();

    logStep(
    'KAKAO',
    '5-C',
    'loginWithKakaoAccount() 반환 성공',
    );
    }


    // ------------------------------------------------
    // OAuthToken 획득 완료
    // ------------------------------------------------

    logStep(
    'KAKAO',
    '6',
    'OAuthToken 객체 획득 완료',
    );

    final String accessToken =
    oauthToken.accessToken;

    logStep(
    'KAKAO',
    '7',
    'Access Token 존재 = '
    '${accessToken.isNotEmpty}, '
    'length=${accessToken.length}',
    );

    // 실제 Access Token은 출력하지 않음


    // ------------------------------------------------
    // Spring 요청
    // ------------------------------------------------

    logStep(
    'KAKAO',
    '8',
    'socialSignup(kakao) 호출 직전',
    );

    await socialSignup(
    socialType: 'kakao',
    token: accessToken,
    );

    logStep(
    'KAKAO',
    '9',
    'socialSignup(kakao) 정상 반환',
    );
    } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '================ KAKAO ERROR ================',
    );

    debugPrint(
    '[KAKAO][ERROR] '
    'type = ${e.runtimeType}',
    );

    debugPrint(
    '[KAKAO][ERROR] '
    'error = $e',
    );

    debugPrint(
    '[KAKAO][ERROR] '
    'stackTrace = $stackTrace',
    );

    debugPrint(
    '=============================================',
    );

    if (!mounted) {
    return;
    }

    setState(() {
    result =
    'Kakao 회원가입 실패\n\n'
    '${e.runtimeType}\n'
    '$e';
    });
    } finally {
    logStep(
    'KAKAO',
    '10',
    'finally 진입',
    );

    if (mounted) {
    setState(() {
    loading = false;
    });
    }

    logStep(
    'KAKAO',
    '11',
    'kakaoSignup() 종료',
    );
    }
  }


  // ====================================================
  // Google / Kakao 공통 Spring 회원가입 요청
  // ====================================================

  Future<void> socialSignup({
    required String socialType,
    required String token,
  }) async {
    logStep(
      'SPRING',
      '1',
      'socialSignup() 시작 '
          '- socialType=$socialType',
    );

    try {
      if (mounted) {
        setState(() {
          result =
          '$socialType 인증 성공\n'
              'Spring 회원가입 요청 중...';
        });
      }


      // ------------------------------------------------
      // URL 생성
      // ------------------------------------------------

      final Uri uri =
      Uri.parse(
        '$springBaseUrl/auth/signup/$socialType',
      );

      logStep(
        'SPRING',
        '2',
        '요청 URL = $uri',
      );


      // ------------------------------------------------
      // Request Body 생성
      // ------------------------------------------------

      final Map<String, dynamic> body = {
        // Google:
        // Google ID Token
        //
        // Kakao:
        // Kakao Access Token
        'token': token,

        'name': '소셜회원',

        'birth': '2000-01-01',

        // Spring NationCode에 실제 존재하는 값
        'nation': 'KR',

        // 실제 DB의 ID와 맞춰야 함
        'motherTongueId': 1,
        'learningLanguageId': 2,
        'learningLanguageLevelId': 1,

        'serviceUseTermId': 1,
        'personalInfoUseTermId': 2,

        'isServiceUseTermAgreed': true,
        'isPersonalInfoUseTermAgreed': true,
      };


      logStep(
        'SPRING',
        '3',
        'Request Body 생성 완료',
      );


      // 토큰을 제외한 데이터만 출력

      debugPrint(
        '[SPRING][3] '
            'name=${body['name']}, '
            'birth=${body['birth']}, '
            'nation=${body['nation']}',
      );

      debugPrint(
        '[SPRING][3] '
            'motherTongueId='
            '${body['motherTongueId']}, '
            'learningLanguageId='
            '${body['learningLanguageId']}, '
            'learningLanguageLevelId='
            '${body['learningLanguageLevelId']}',
      );

      debugPrint(
        '[SPRING][3] '
            'serviceUseTermId='
            '${body['serviceUseTermId']}, '
            'personalInfoUseTermId='
            '${body['personalInfoUseTermId']}',
      );


      logStep(
        'SPRING',
        '4',
        'http.post() 호출 직전',
      );


      // ------------------------------------------------
      // Flutter → Spring 실제 HTTP 요청
      // ------------------------------------------------

      final http.Response response =
      await http.post(
        uri,
        headers: {
          'Content-Type':
          'application/json',
        },
        body: jsonEncode(body),
      );


      // Spring으로부터 HTTP 응답을 받음

      logStep(
        'SPRING',
        '5',
        'http.post() 응답 수신',
      );

      debugPrint(
        '[SPRING][6] '
            'HTTP STATUS = '
            '${response.statusCode}',
      );

      debugPrint(
        '[SPRING][7] '
            'RESPONSE BODY = '
            '${response.body}',
      );


      // ------------------------------------------------
      // 성공 응답
      // ------------------------------------------------

      if (
      response.statusCode >= 200 &&
          response.statusCode < 300
      ) {
        logStep(
          'SPRING',
          '8',
          'Spring 회원가입 HTTP 성공',
        );

        if (!mounted) {
          return;
        }

        setState(() {
          result =
          '$socialType 회원가입 성공\n\n'
              'HTTP ${response.statusCode}\n\n'
              '${response.body}';
        });

        logStep(
          'SPRING',
          '9',
          'socialSignup() 정상 종료',
        );

        return;
      }


      // ------------------------------------------------
      // 4xx / 5xx
      // ------------------------------------------------

      logStep(
        'SPRING',
        'ERROR',
        'Spring이 실패 HTTP 응답 반환',
      );

      throw Exception(
        'Spring 요청 실패\n'
            'HTTP ${response.statusCode}\n'
            '${response.body}',
      );
    } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '================ SPRING ERROR ================',
    );

    debugPrint(
    '[SPRING][ERROR] '
    'type = ${e.runtimeType}',
    );

    debugPrint(
    '[SPRING][ERROR] '
    'error = $e',
    );

    debugPrint(
    '[SPRING][ERROR] '
    'stackTrace = $stackTrace',
    );

    debugPrint(
    '==============================================',
    );

    // Google/Kakao 쪽에서도 처리하도록
    // 다시 예외 전달
    rethrow;
    }
  }


  // ====================================================
  // UI
  // ====================================================

  @override
  Widget build(
      BuildContext context,
      ) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Language World Social Signup',
        ),
      ),

      body: SingleChildScrollView(
        padding:
        const EdgeInsets.all(24),

        child: Column(
          crossAxisAlignment:
          CrossAxisAlignment.stretch,

          children: [
            // ==========================================
            // Google 버튼
            // ==========================================

            ElevatedButton(
              onPressed:
              loading
                  ? null
                  : googleSignup,
              child:
              const Text(
                'Google 회원가입',
              ),
            ),

            const SizedBox(
              height: 16,
            ),


            // ==========================================
            // Kakao 버튼
            // ==========================================

            ElevatedButton(
              onPressed:
              loading
                  ? null
                  : kakaoSignup,
              child:
              const Text(
                'Kakao 회원가입',
              ),
            ),

            const SizedBox(
              height: 32,
            ),


            const Text(
              '현재 상태',
              style: TextStyle(
                fontSize: 18,
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(
              height: 12,
            ),

            SelectableText(
              result,
            ),

            const SizedBox(
              height: 32,
            ),
          ],
        ),
      ),
    );
  }
}