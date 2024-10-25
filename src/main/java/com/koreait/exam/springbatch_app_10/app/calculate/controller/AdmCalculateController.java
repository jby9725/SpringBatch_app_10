package com.koreait.exam.springbatch_app_10.app.calculate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm/calculate")
public class AdmCalculateController {
    @GetMapping("/makeData")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showMakeData() {
        return "adm/calculate/makeData";
    }
}