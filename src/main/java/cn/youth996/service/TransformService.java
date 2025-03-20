package cn.youth996.service;

/**
 * 转换服务
 *
 * @author Zhan Xinjian
 * @date 2025/3/20
 */
public interface TransformService {
    /**
     * IPV4转换IPV6
     * @param ipv4 ipv4地址
     * @return ipv6地址
     */
    String ipv42v6(String ipv4);
}
