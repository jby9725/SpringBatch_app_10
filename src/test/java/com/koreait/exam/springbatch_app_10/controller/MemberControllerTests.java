package com.koreait.exam.springbatch_app_10.controller;

import com.koreait.exam.springbatch_app_10.app.member.service.MemberService;
import com.koreait.exam.springbatch_app_10.app.member.controller.MemberController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // 테스트 환경이다 알려줌
@AutoConfigureMockMvc // MockMVC 설정 / MockMVC? : 실제 웹서버를 띄우지 않아도 HTTP 요청을 흉내내서 컨트롤러 메서드를 실행
@Transactional // 테스트 메서드 하나하나 끝나면 다시 롤백
@ActiveProfiles("test")
public class MemberControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 폼")
    void t1() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/join")) // member/join GET 요청 -> 폼 줘
                .andDo(print());
        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful()) // 성공(상태코드가 200번대)으로 끝났는지?
                .andExpect(handler().handlerType(MemberController.class)) // 요청을 MemberController가 처리했는지?
                .andExpect(handler().methodName("showJoin")) // 실행된 method가 showJoin인지?
                .andExpect(content().string(containsString("회원가입"))); // 해당 페이지에 "회원가입" 텍스트가 포함되어 있는지?
    }

    @Test
    @DisplayName("회원가입")
    void t2() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join") // member/join POST 요청 해
                        .with(csrf()) // csrf 토큰을 추가해서 보안 검증 통과
                        .param("username", "user999")       // 회원
                        .param("password", "1234")          // 가입
                        .param("email", "user999@test.com") // 데이터
                )
                .andDo(print());
        // THEN
        resultActions
                .andExpect(status().is3xxRedirection()) // 리다이렉션(300번대)이 되었는지 확인
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrlPattern("/member/login?msg=**")); // 리다이렉트 URL 패턴이 "/member/login?msg=**" 형식인지?
        assertThat(memberService.findByUsername("user999").isPresent()).isTrue(); // user999가 잘 가입이 되었는지?
    }
}