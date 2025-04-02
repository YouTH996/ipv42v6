package cn.youth996.service.impl;

import cn.youth996.service.TransformService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhan Xinjian
 * @date 2025/3/20
 */
@Slf4j
@Service
public class TransformServiceImpl implements TransformService {

    /**
     * 根据IPv4地址获取IPv6地址
     *
     * @param ipv4Address IPv4 地址
     * @return IPV6 地址
     */
    private String ipv42v6ByWin(String ipv4Address) {
        try {
            //先获取设备的MAC地址
            Process ipv4Process = Runtime.getRuntime().exec("netsh interface ipv4 show neighbors");
            BufferedReader ipv4reader = new BufferedReader(new InputStreamReader(ipv4Process.getInputStream(), StandardCharsets.UTF_8));
            String ipv4Line;
            String macAddress = "";
            while ((ipv4Line = ipv4reader.readLine()) != null) {
                if (ipv4Line.contains(ipv4Address)) { // 过滤匹配的 IPv4 地址
                    String macRegex = "([0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(?:\\2[0-9A-Fa-f]{2}){4})";
                    Pattern pattern = Pattern.compile(macRegex);
                    Matcher matcher = pattern.matcher(ipv4Line);
                    if (matcher.find()) {
                        macAddress = matcher.group(1);
                        log.info("找到对应的 MAC 地址: " + macAddress);
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(macAddress)) {
                Process process = Runtime.getRuntime().exec("netsh interface ipv6 show neighbors");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(macAddress)) { // 过滤匹配的 IPv4 地址
                        log.info("找到对应的 IPv6 地址: " + line);
                        String ipv6Regex = "([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,6}::?[0-9A-Fa-f]{0,4}(:[0-9A-Fa-f]{1,4}){0,6})";
                        Pattern pattern = Pattern.compile(ipv6Regex);
                        Matcher matcher = pattern.matcher(line);

                        if (matcher.find()) {
                            String ipv6Address = matcher.group(1);
                            log.info("提取的 IPv6 地址: " + ipv6Address);
                            return ipv6Address;
                        } else {
                            log.info("未找到 IPv6 地址");
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据IPv4地址获取IPv6地址
     *
     * @param ipv4Address IPv4 地址
     * @return IPV6 地址
     */
    private String ipv42v6ByLinux(String ipv4Address) {
        try {
            // 1.获取网卡名称
            String networkInterface = getNetworkInterfaceByIp(ipv4Address);
            // 2. 通过 ping 触发 IPv6 记录
            Runtime.getRuntime().exec("ping6 -c 1 ff02::1%"+networkInterface); // eth0 替换为你的网卡
            Thread.sleep(1000); // 等待邻居表更新
            //先进行ping通测试
            Runtime.getRuntime().exec("ping "+ipv4Address);
            Thread.sleep(1000); // 等待ping更新
            // 获取设备的 MAC 地址
            Process ipv4Process = Runtime.getRuntime().exec("ip -4 neigh");
            BufferedReader ipv4Reader = new BufferedReader(new InputStreamReader(ipv4Process.getInputStream(), StandardCharsets.UTF_8));
            String ipv4Line;
            String macAddress = "";

            // 查找匹配的 IPv4 地址，获取 MAC 地址
            while ((ipv4Line = ipv4Reader.readLine()) != null) {
                if (ipv4Line.contains(ipv4Address)) {
                    // 解析 MAC 地址
                    String macRegex = "([0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(?:\\2[0-9A-Fa-f]{2}){4})";
                    Pattern pattern = Pattern.compile(macRegex);
                    Matcher matcher = pattern.matcher(ipv4Line);
                    if (matcher.find()) {
                        macAddress = matcher.group(1);
                        log.info("找到对应的 MAC 地址: " + macAddress);
                        break;
                    }
                }
            }

            // 如果找到 MAC 地址，获取对应的 IPv6 地址
            if (StringUtils.isNotBlank(macAddress)) {
                Process ipv6Process = Runtime.getRuntime().exec("ip -6 neigh");
                BufferedReader ipv6Reader = new BufferedReader(new InputStreamReader(ipv6Process.getInputStream(), StandardCharsets.UTF_8));
                String line;

                // 查找匹配的 MAC 地址，获取 IPv6 地址
                while ((line = ipv6Reader.readLine()) != null) {
                    if (line.contains(macAddress)) {
                        log.info("找到对应的 IPv6 地址: " + line);
                        String ipv6Regex = "([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,6}::?[0-9A-Fa-f]{0,4}(:[0-9A-Fa-f]{1,4}){0,6})";
                        Pattern ipv6Pattern = Pattern.compile(ipv6Regex);
                        Matcher ipv6Matcher = ipv6Pattern.matcher(line);

                        // 提取匹配到的 IPv6 地址
                        if (ipv6Matcher.find()) {
                            String ipv6Address = ipv6Matcher.group(1);
                            log.info("提取的 IPv6 地址: " + ipv6Address);
                            return ipv6Address;
                        } else {
                            log.info("未找到 IPv6 地址");
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取网卡名称
     * @param ipv4Address IPv4 地址
     * @return 网卡名称
     */
    private String getNetworkInterfaceByIp(String ipv4Address) {
        try {
            // 运行 `ip route get <IPv4地址>` 命令
            Process process = Runtime.getRuntime().exec("ip route get " + ipv4Address);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = Pattern.compile("dev (\\S+)").matcher(line);
                if (matcher.find()) {
                    String interfaceName = matcher.group(1);
                    log.info("找到网卡: " + interfaceName);
                    return interfaceName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String ipv42v6(String ipv4) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return ipv42v6ByWin(ipv4);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return ipv42v6ByLinux(ipv4);
        } else {
            log.error("当前操作系统未知");
            return null;
        }
    }
}
