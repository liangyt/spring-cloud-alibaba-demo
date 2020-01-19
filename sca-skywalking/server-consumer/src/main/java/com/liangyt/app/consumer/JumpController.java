package com.liangyt.app.consumer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述：测试跳转
 * 作者：liangyongtong
 * 日期：2019/11/22 1:12 PM
 */
@Controller
public class JumpController {


    @GetMapping("/jump")
    public void response(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://www.baidu.com");
    }
}
