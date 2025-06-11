package com.projectwz.partsforecast.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController // 标记这个类是一个 RESTful 控制器，@Controller 和 @ResponseBody 的组合
public class HelloController {

    @GetMapping("/api/hello") // 将 HTTP GET 请求映射到 /api/hello 路径
    public Map<String, String> sayHello(@RequestParam(name = "name", defaultValue = "World") String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        return response; // Spring Boot 会自动将 Map 转换为 JSON 响应
    }
}
