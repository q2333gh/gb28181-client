package com.wydpp.gb28181.schedule;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 注册定时任务
 * 
 * sip-c 向 sip-s 的注册任务
 */
@Component
public class DeviceRegisterSchedule {

    private Logger logger = LoggerFactory.getLogger(DeviceRegisterSchedule.class);

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SIPCommander sipCommander;

    /**
     * 设备注册时间超时定时任务
     * sched: 该任务在应用启动后延迟3秒执行，之后每120秒执行一次，用于检查设备注册状态并在即将过期时重新注册
     */
    @Scheduled(initialDelay = 3, fixedDelay = 120, timeUnit = TimeUnit.SECONDS)
    public void checkSipDeviceStatus() {
        logger.info("检查设备注册状态：isNeedRegister={}, registerTime={}",
                sipDevice.isNeedRegister(), sipDevice.getRegisterTime());
        if (sipDevice.isNeedRegister() && sipDevice.getRegisterTime() != null) {
            int expires = sipDevice.getExpires() * 1000;
            long registerDate = sipDevice.getRegisterTime();
            logger.info("检查设备注册状态：当前时间 {} - 注册时间 {} = {}ms，过期时间：{}ms",
                    System.currentTimeMillis(), registerDate, 
                    System.currentTimeMillis() - registerDate, expires);
            if (System.currentTimeMillis() - registerDate >= expires - 10) {
                logger.info("设备注册即将过期，重新发起注册请求");
                sipCommander.register(sipPlatform, sipDevice, eventResult -> {
                    long time = System.currentTimeMillis();
                    sipDevice.setOnline(true);
                    sipDevice.setRegisterTime(time);
                    sipDevice.setNeedRegister(true);
                    sipDevice.setKeepaliveTime(time);
                    logger.info("设备重新注册成功，更新注册时间：{}", time);
                });
            }
        }
    }
}
