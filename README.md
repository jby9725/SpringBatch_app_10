### 공부 메모

application.yml : 애플리케이션 기본 설정
- 원래는 설정 파일을 하나만 썼지만 이제 환경별로 설정을 나누어 해볼것이다.
- dev(개발), prod(운영), test(테스트) 환경

application.yml 에 mail 환경 내 것으로 꼭 변경할 것.

---

### 공부 메모

@PreAuthorize("isAnonymous()") // 인증되지 않았을 떄만 실행 가능 -> 로그인 X
@Valid : 유효성 검사