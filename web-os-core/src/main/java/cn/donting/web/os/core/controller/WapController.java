package cn.donting.web.os.core.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WapController {


    @GetMapping("/error1")
    public String error(@RequestParam("a") int a) {
        throw new RuntimeException("error");
    }

    @GetMapping("/test")
    public Map test(@RequestParam("a") int a) {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("a", a);
        objectObjectHashMap.put("test", "adas");
        return objectObjectHashMap;
    }
}
