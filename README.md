### 공부 메모

application.yml : 애플리케이션 기본 설정
- 원래는 설정 파일을 하나만 썼지만 이제 환경별로 설정을 나누어 해볼것이다.
- dev(개발), prod(운영), test(테스트) 환경

application.yml 에 mail 환경 내 것으로 꼭 변경할 것.

---

### 공부 메모

@PreAuthorize("isAnonymous()") // 인증되지 않았을 떄만 실행 가능 -> 로그인 X
@Valid : 유효성 검사

---

시나리오
- 본인이 만든 음원(내 음원)은 나만 볼 수 있다.
- 내 상품은 남들도 볼 수 있다.
- 본인이 만든 음원을 상품화 시킬 수 있다.
- 단, 내가 만든 상품은 내가 장바구니에 담을 수 없다.
- Cash 충전금으로 결제 (내부 화폐처럼)
- 정산까지.

---

상품 등록 create 후 db에 save
상품 등록 후 옵션 변경도 할 수 있도록 modify

---

상품 등록,수정 -> 장바구니, 내부 화폐 도입 -> 주문, 환불 기능 -> 주문 상세 보기

---

const tossPayments = TossPayments("payments 개발자센터 -> 내 개발 정보 -> API 개별 연동 키 -> 클라이언트 키");

private final String SECRET_KEY = "payments 개발자센터 -> 내 개발 정보 -> API 개별 연동 키 -> 시크릿 키";