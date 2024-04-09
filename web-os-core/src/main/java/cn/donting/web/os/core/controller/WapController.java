package cn.donting.web.os.core.controller;

import cn.donting.web.os.web.annotation.GetMapping;
import cn.donting.web.os.web.annotation.RequestParam;
import cn.donting.web.os.web.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WapController {


    @GetMapping("/error")
    public String error(@RequestParam("a") int a) {
        throw new RuntimeException("error");
    }
    @GetMapping("/test")
    public Map test(@RequestParam("a") int a) {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("a",a);
        objectObjectHashMap.put("test","adas");
        return objectObjectHashMap;
    }
}
