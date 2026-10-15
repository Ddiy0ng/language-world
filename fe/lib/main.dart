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
  String.fromEnvironment('GOOGLE_WEB_CLIENT_ID');

  static const String kakaoNativeAppKey =
  String.fromEnvironment('KAKAO_NATIVE_APP_KEY');

  static const String springBaseUrl =
  String.fromEnvironment('SPRING_BASE_URL');
}

const String googleWebClientId =
    SecureConfig.googleWebClientId;

const String kakaoNativeAppKey =
    SecureConfig.kakaoNativeAppKey;

const String springBaseUrl =
    SecureConfig.springBaseUrl;


// ======================================================
// 로그
// ======================================================

void logStep(
    String scope,
    String step,
    String message,
    ) {
  debugPrint('[$scope][$step] $message');
}


// ======================================================
// main
// ======================================================

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

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
// App
// ======================================================

class MyApp extends StatelessWidget {
  const MyApp({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Language World Auth Test',
      theme: ThemeData(
        useMaterial3: true,
      ),
      home: const AuthTestPage(),
    );
  }
}


// ======================================================
// 인증 테스트 화면
// ======================================================

class AuthTestPage extends StatefulWidget {
  const AuthTestPage({
    super.key,
  });

  @override
  State<AuthTestPage> createState() =>
      _AuthTestPageState();
}


class _AuthTestPageState
    extends State<AuthTestPage> {

  // ====================================================
  // 회원가입 Form
  // ====================================================

  final GlobalKey<FormState> _signupFormKey =
  GlobalKey<FormState>();

  final TextEditingController _nameController =
  TextEditingController();

  final TextEditingController _birthController =
  TextEditingController();

  final TextEditingController _nationController =
  TextEditingController(
    text: 'KR',
  );

  final TextEditingController _motherTongueIdController =
  TextEditingController(
    text: '1',
  );

  final TextEditingController
  _learningLanguageIdController =
  TextEditingController(
    text: '2',
  );

  final TextEditingController
  _learningLanguageLevelIdController =
  TextEditingController(
    text: '1',
  );

  final TextEditingController _serviceUseTermIdController =
  TextEditingController(
    text: '1',
  );

  final TextEditingController
  _personalInfoUseTermIdController =
  TextEditingController(
    text: '2',
  );

  bool _serviceTermAgreed = false;
  bool _personalInfoTermAgreed = false;

  bool _loading = false;

  String _result =
      '아직 요청하지 않았습니다.';


  // ====================================================
  // dispose
  // ====================================================

  @override
  void dispose() {
    _nameController.dispose();
    _birthController.dispose();
    _nationController.dispose();
    _motherTongueIdController.dispose();
    _learningLanguageIdController.dispose();
    _learningLanguageLevelIdController.dispose();
    _serviceUseTermIdController.dispose();
    _personalInfoUseTermIdController.dispose();

    super.dispose();
  }


  // ====================================================
  // 생년월일 선택
  // ====================================================

  Future<void> _selectBirth() async {
    final DateTime? date =
    await showDatePicker(
      context: context,
      initialDate: DateTime(
        2000,
        1,
        1,
      ),
      firstDate: DateTime(
        1900,
        1,
        1,
      ),
      lastDate: DateTime.now(),
    );

    if (date == null) {
      return;
    }

    final String year =
    date.year.toString().padLeft(4, '0');

    final String month =
    date.month.toString().padLeft(2, '0');

    final String day =
    date.day.toString().padLeft(2, '0');

    _birthController.text =
    '$year-$month-$day';
  }


  // ====================================================
  // Form validator
  // ====================================================

  String? _requiredValidator(
      String? value,
      ) {
    if (value == null ||
        value.trim().isEmpty) {
      return '필수 입력값입니다.';
    }

    return null;
  }


  String? _idValidator(
      String? value,
      ) {
    if (value == null ||
        value.trim().isEmpty) {
      return '필수 입력값입니다.';
    }

    final int? number =
    int.tryParse(
      value.trim(),
    );

    if (number == null ||
        number <= 0) {
      return '1 이상의 숫자를 입력하세요.';
    }

    return null;
  }


  // ====================================================
  // Provider Token 획득
  // ====================================================

  Future<String> _getSocialToken(
      String socialType,
      ) async {
    switch (socialType) {
      case 'google':
        return _getGoogleIdToken();

      case 'kakao':
        return _getKakaoAccessToken();

      default:
        throw Exception(
          '지원하지 않는 socialType입니다: '
              '$socialType',
        );
    }
  }


  // ====================================================
  // Google Token
  // ====================================================

  Future<String> _getGoogleIdToken() async {
    logStep(
      'GOOGLE',
      '1',
      'Google 인증 시작',
    );

    logStep(
      'GOOGLE',
      '2',
      'authenticate() 호출 직전',
    );

    final GoogleSignInAccount account =
    await GoogleSignIn
        .instance
        .authenticate();

    logStep(
      'GOOGLE',
      '3',
      'authenticate() 반환 성공',
    );

    final String? idToken =
        account
            .authentication
            .idToken;

    if (idToken == null ||
        idToken.isEmpty) {
      throw Exception(
        'Google ID Token을 '
            '받지 못했습니다.',
      );
    }

    logStep(
      'GOOGLE',
      '4',
      'ID Token 획득 성공 '
          'length=${idToken.length}',
    );

    // 실제 token 문자열 출력 금지

    return idToken;
  }


  // ====================================================
  // Kakao Token
  // ====================================================

  Future<String>
  _getKakaoAccessToken() async {

    logStep(
      'KAKAO',
      '1',
      'Kakao 인증 시작',
    );

    final bool installed =
    await isKakaoTalkInstalled();

    logStep(
      'KAKAO',
      '2',
      'KakaoTalk 설치 여부 = '
          '$installed',
    );

    OAuthToken oauthToken;

    if (installed) {
      try {
        logStep(
          'KAKAO',
          '3-A',
          'loginWithKakaoTalk() 호출',
        );

        oauthToken =
        await UserApi
            .instance
            .loginWithKakaoTalk();

        logStep(
          'KAKAO',
          '4-A',
          'KakaoTalk 로그인 성공',
        );
      } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '[KAKAO][TALK ERROR] $e',
    );

    debugPrint(
    '$stackTrace',
    );

    logStep(
    'KAKAO',
    '3-B',
    'KakaoAccount fallback',
    );

    oauthToken =
    await UserApi
        .instance
        .loginWithKakaoAccount();

    logStep(
    'KAKAO',
    '4-B',
    'KakaoAccount 로그인 성공',
    );
    }
    } else {
    logStep(
    'KAKAO',
    '3-C',
    'loginWithKakaoAccount() 호출',
    );

    oauthToken =
    await UserApi
        .instance
        .loginWithKakaoAccount();

    logStep(
    'KAKAO',
    '4-C',
    'KakaoAccount 로그인 성공',
    );
    }

    final String accessToken =
    oauthToken.accessToken;

    if (accessToken.isEmpty) {
    throw Exception(
    'Kakao Access Token을 '
    '받지 못했습니다.',
    );
    }

    logStep(
    'KAKAO',
    '5',
    'Access Token 획득 성공 '
    'length=${accessToken.length}',
    );

    // 실제 token 문자열 출력 금지

    return accessToken;
  }


  // ====================================================
  // 회원가입
  // ====================================================

  Future<void> _socialSignup(
      String socialType,
      ) async {

    if (_loading) {
      return;
    }


    // --------------------------------------------------
    // 입력값 검증
    // --------------------------------------------------

    final bool valid =
        _signupFormKey
            .currentState
            ?.validate() ??
            false;

    if (!valid) {
      return;
    }


    if (!_serviceTermAgreed) {
      _showMessage(
        '서비스 이용약관에 '
            '동의해야 합니다.',
      );

      return;
    }


    if (!_personalInfoTermAgreed) {
      _showMessage(
        '개인정보 이용약관에 '
            '동의해야 합니다.',
      );

      return;
    }


    setState(() {
      _loading = true;
      _result =
      '$socialType 회원가입 인증 중...';
    });


    try {
      logStep(
        'SIGNUP',
        '1',
        '$socialType 회원가입 시작',
      );


      // ------------------------------------------------
      // Google / Kakao 인증
      // ------------------------------------------------

      final String token =
      await _getSocialToken(
        socialType,
      );


      // ------------------------------------------------
      // 입력값 → JSON
      // ------------------------------------------------

      final Map<String, dynamic> body = {
        'token': token,

        'name':
        _nameController
            .text
            .trim(),

        'birth':
        _birthController
            .text
            .trim(),

        'nation':
        _nationController
            .text
            .trim()
            .toUpperCase(),

        'motherTongueId':
        int.parse(
          _motherTongueIdController
              .text
              .trim(),
        ),

        'learningLanguageId':
        int.parse(
          _learningLanguageIdController
              .text
              .trim(),
        ),

        'learningLanguageLevelId':
        int.parse(
          _learningLanguageLevelIdController
              .text
              .trim(),
        ),

        'serviceUseTermId':
        int.parse(
          _serviceUseTermIdController
              .text
              .trim(),
        ),

        'personalInfoUseTermId':
        int.parse(
          _personalInfoUseTermIdController
              .text
              .trim(),
        ),

        'isServiceUseTermAgreed':
        _serviceTermAgreed,

        'isPersonalInfoUseTermAgreed':
        _personalInfoTermAgreed,
      };


      final Uri uri =
      Uri.parse(
        '$springBaseUrl/auth/signup/'
            '$socialType',
      );

      logStep(
        'SIGNUP',
        '2',
        'POST $uri',
      );

      logStep(
        'SIGNUP',
        '3',
        '회원가입 Body 생성 완료',
      );


      // token 제외 데이터만 로그

      debugPrint(
        '[SIGNUP] '
            'name=${body['name']}',
      );

      debugPrint(
        '[SIGNUP] '
            'birth=${body['birth']}',
      );

      debugPrint(
        '[SIGNUP] '
            'nation=${body['nation']}',
      );


      final http.Response response =
      await http.post(
        uri,
        headers: {
          'Content-Type':
          'application/json',
        },
        body: jsonEncode(body),
      );


      logStep(
        'SIGNUP',
        '4',
        'HTTP ${response.statusCode}',
      );


      if (response.statusCode >= 200 &&
          response.statusCode < 300) {

        if (!mounted) {
          return;
        }

        setState(() {
          _result =
          '$socialType 회원가입 성공\n'
              'HTTP ${response.statusCode}';
        });

        return;
      }


      throw Exception(
        '회원가입 실패\n'
            'HTTP ${response.statusCode}\n'
            '${response.body}',
      );
    } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '============= SIGNUP ERROR =============',
    );

    debugPrint(
    '[SIGNUP][ERROR] '
    '${e.runtimeType}',
    );

    debugPrint(
    '[SIGNUP][ERROR] $e',
    );

    debugPrint(
    '[SIGNUP][STACK] '
    '$stackTrace',
    );

    debugPrint(
    '========================================',
    );

    if (!mounted) {
    return;
    }

    setState(() {
    _result =
    '$socialType 회원가입 실패\n\n'
    '$e';
    });
    } finally {
    if (mounted) {
    setState(() {
    _loading = false;
    });
    }
    }
  }


  // ====================================================
  // 로그인
  // ====================================================

  Future<void> _socialLogin(
      String socialType,
      ) async {

    if (_loading) {
      return;
    }

    setState(() {
      _loading = true;
      _result =
      '$socialType 로그인 인증 중...';
    });

    try {
      logStep(
        'LOGIN',
        '1',
        '$socialType 로그인 시작',
      );


      // ------------------------------------------------
      // Provider 인증
      // ------------------------------------------------

      final String token =
      await _getSocialToken(
        socialType,
      );


      // ------------------------------------------------
      // Spring 로그인 요청
      // ------------------------------------------------

      final Uri uri =
      Uri.parse(
        '$springBaseUrl/auth/login/'
            '$socialType',
      );


      final Map<String, dynamic> body = {
        'token': token,
      };


      logStep(
        'LOGIN',
        '2',
        'POST $uri',
      );


      final http.Response response =
      await http.post(
        uri,
        headers: {
          'Content-Type':
          'application/json',
        },
        body: jsonEncode(body),
      );


      logStep(
        'LOGIN',
        '3',
        'HTTP ${response.statusCode}',
      );


      if (response.statusCode >= 200 &&
          response.statusCode < 300) {

        // 로그인 성공 응답에는
        // accessToken / refreshToken 등
        // JWT가 포함될 수 있으므로
        // response.body를 debugPrint 하지 않음.

        if (!mounted) {
          return;
        }

        setState(() {
          _result =
          '$socialType 로그인 성공\n'
              'HTTP ${response.statusCode}\n\n'
              'Spring JWT 응답 수신 완료';
        });

        return;
      }


      throw Exception(
        '로그인 실패\n'
            'HTTP ${response.statusCode}\n'
            '${response.body}',
      );
    } catch (
    e,
    stackTrace,
    ) {
    debugPrint(
    '============= LOGIN ERROR =============',
    );

    debugPrint(
    '[LOGIN][ERROR] '
    '${e.runtimeType}',
    );

    debugPrint(
    '[LOGIN][ERROR] $e',
    );

    debugPrint(
    '[LOGIN][STACK] '
    '$stackTrace',
    );

    debugPrint(
    '=======================================',
    );

    if (!mounted) {
    return;
    }

    setState(() {
    _result =
    '$socialType 로그인 실패\n\n'
    '$e';
    });
    } finally {
    if (mounted) {
    setState(() {
    _loading = false;
    });
    }
    }
  }


  // ====================================================
  // SnackBar
  // ====================================================

  void _showMessage(
      String message,
      ) {
    ScaffoldMessenger
        .of(context)
        .showSnackBar(
      SnackBar(
        content: Text(
          message,
        ),
      ),
    );
  }


  // ====================================================
  // 공통 TextField
  // ====================================================

  Widget _buildTextField({
    required String label,
    required TextEditingController controller,
    String? hint,
    TextInputType? keyboardType,
    String? Function(String?)? validator,
    bool readOnly = false,
    VoidCallback? onTap,
  }) {
    return Padding(
      padding:
      const EdgeInsets.only(
        bottom: 16,
      ),
      child: TextFormField(
        controller: controller,
        keyboardType:
        keyboardType,
        validator:
        validator,
        readOnly:
        readOnly,
        onTap:
        onTap,
        decoration:
        InputDecoration(
          labelText: label,
          hintText: hint,
          border:
          const OutlineInputBorder(),
        ),
      ),
    );
  }


  // ====================================================
  // 회원가입 화면
  // ====================================================

  Widget _buildSignupPage() {
    return SingleChildScrollView(
      padding:
      const EdgeInsets.all(
        24,
      ),
      child: Form(
        key:
        _signupFormKey,
        child: Column(
          crossAxisAlignment:
          CrossAxisAlignment.stretch,
          children: [

            const Text(
              '회원가입 정보',
              style: TextStyle(
                fontSize: 22,
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(
              height: 24,
            ),


            // ------------------------------------------
            // 이름
            // ------------------------------------------

            _buildTextField(
              label: '이름',
              controller:
              _nameController,
              hint: '홍길동',
              validator:
              _requiredValidator,
            ),


            // ------------------------------------------
            // 생년월일
            // ------------------------------------------

            _buildTextField(
              label:
              '생년월일',
              controller:
              _birthController,
              hint:
              '2000-01-01',
              validator:
              _requiredValidator,
              readOnly:
              true,
              onTap:
              _selectBirth,
            ),


            // ------------------------------------------
            // 국가
            // ------------------------------------------

            _buildTextField(
              label:
              '국가 코드',
              controller:
              _nationController,
              hint:
              'KR',
              validator:
              _requiredValidator,
            ),


            const Divider(
              height: 32,
            ),

            const Text(
              '언어 설정',
              style: TextStyle(
                fontSize: 18,
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(
              height: 16,
            ),


            _buildTextField(
              label:
              '모국어 ID',
              controller:
              _motherTongueIdController,
              keyboardType:
              TextInputType.number,
              validator:
              _idValidator,
            ),


            _buildTextField(
              label:
              '학습 언어 ID',
              controller:
              _learningLanguageIdController,
              keyboardType:
              TextInputType.number,
              validator:
              _idValidator,
            ),


            _buildTextField(
              label:
              '학습 언어 Level ID',
              controller:
              _learningLanguageLevelIdController,
              keyboardType:
              TextInputType.number,
              validator:
              _idValidator,
            ),


            const Divider(
              height: 32,
            ),

            const Text(
              '약관',
              style: TextStyle(
                fontSize: 18,
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(
              height: 16,
            ),


            _buildTextField(
              label:
              '서비스 이용약관 ID',
              controller:
              _serviceUseTermIdController,
              keyboardType:
              TextInputType.number,
              validator:
              _idValidator,
            ),


            _buildTextField(
              label:
              '개인정보 이용약관 ID',
              controller:
              _personalInfoUseTermIdController,
              keyboardType:
              TextInputType.number,
              validator:
              _idValidator,
            ),


            CheckboxListTile(
              value:
              _serviceTermAgreed,
              title:
              const Text(
                '서비스 이용약관 동의',
              ),
              controlAffinity:
              ListTileControlAffinity.leading,
              contentPadding:
              EdgeInsets.zero,
              onChanged:
              _loading
                  ? null
                  : (bool? value) {
                setState(() {
                  _serviceTermAgreed =
                      value ?? false;
                });
              },
            ),


            CheckboxListTile(
              value:
              _personalInfoTermAgreed,
              title:
              const Text(
                '개인정보 이용 동의',
              ),
              controlAffinity:
              ListTileControlAffinity.leading,
              contentPadding:
              EdgeInsets.zero,
              onChanged:
              _loading
                  ? null
                  : (bool? value) {
                setState(() {
                  _personalInfoTermAgreed =
                      value ?? false;
                });
              },
            ),


            const SizedBox(
              height: 24,
            ),


            // ------------------------------------------
            // Google 회원가입
            // ------------------------------------------

            FilledButton(
              onPressed:
              _loading
                  ? null
                  : () {
                _socialSignup(
                  'google',
                );
              },
              child:
              const Text(
                'Google로 회원가입',
              ),
            ),


            const SizedBox(
              height: 12,
            ),


            // ------------------------------------------
            // Kakao 회원가입
            // ------------------------------------------

            FilledButton.tonal(
              onPressed:
              _loading
                  ? null
                  : () {
                _socialSignup(
                  'kakao',
                );
              },
              child:
              const Text(
                'Kakao로 회원가입',
              ),
            ),


            const SizedBox(
              height: 32,
            ),

            _buildResult(),
          ],
        ),
      ),
    );
  }


  // ====================================================
  // 로그인 화면
  // ====================================================

  Widget _buildLoginPage() {
    return SingleChildScrollView(
      padding:
      const EdgeInsets.all(
        24,
      ),
      child: Column(
        crossAxisAlignment:
        CrossAxisAlignment.stretch,
        children: [

          const Text(
            '소셜 로그인',
            style: TextStyle(
              fontSize: 22,
              fontWeight:
              FontWeight.bold,
            ),
          ),

          const SizedBox(
            height: 12,
          ),

          const Text(
            '이미 회원가입한 계정으로 '
                'Google 또는 Kakao 인증을 진행합니다.',
          ),

          const SizedBox(
            height: 32,
          ),


          // --------------------------------------------
          // Google 로그인
          // --------------------------------------------

          FilledButton(
            onPressed:
            _loading
                ? null
                : () {
              _socialLogin(
                'google',
              );
            },
            child:
            const Text(
              'Google 로그인',
            ),
          ),


          const SizedBox(
            height: 16,
          ),


          // --------------------------------------------
          // Kakao 로그인
          // --------------------------------------------

          FilledButton.tonal(
            onPressed:
            _loading
                ? null
                : () {
              _socialLogin(
                'kakao',
              );
            },
            child:
            const Text(
              'Kakao 로그인',
            ),
          ),


          const SizedBox(
            height: 32,
          ),


          if (_loading)
            const Center(
              child:
              CircularProgressIndicator(),
            ),


          const SizedBox(
            height: 24,
          ),

          _buildResult(),
        ],
      ),
    );
  }


  // ====================================================
  // 결과 영역
  // ====================================================

  Widget _buildResult() {
    return Card(
      child: Padding(
        padding:
        const EdgeInsets.all(
          16,
        ),
        child: Column(
          crossAxisAlignment:
          CrossAxisAlignment.start,
          children: [

            const Text(
              '현재 상태',
              style: TextStyle(
                fontSize: 17,
                fontWeight:
                FontWeight.bold,
              ),
            ),

            const SizedBox(
              height: 12,
            ),

            SelectableText(
              _result,
            ),
          ],
        ),
      ),
    );
  }


  // ====================================================
  // build
  // ====================================================

  @override
  Widget build(
      BuildContext context,
      ) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title:
          const Text(
            'Language World Auth Test',
          ),
          bottom:
          const TabBar(
            tabs: [
              Tab(
                text:
                '회원가입',
              ),
              Tab(
                text:
                '로그인',
              ),
            ],
          ),
        ),

        body: TabBarView(
          children: [
            _buildSignupPage(),
            _buildLoginPage(),
          ],
        ),
      ),
    );
  }
}