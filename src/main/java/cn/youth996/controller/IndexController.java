package cn.youth996.controller;

import cn.youth996.service.TransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller
 *
 * @author Zhan Xinjian
 * @date 2025/3/20
 */
@RestController
public class IndexController {
    @Autowired
    private TransformService transformService;

    /**
     * 单个IPV4转IPV6
     *
     * @param ipv4 ipv4地址
     * @return IPV6地址
     */
    @GetMapping("/ipv42v6")
    public String ipv42v6(String ipv4) {
        return transformService.ipv42v6(ipv4);
    }
}
