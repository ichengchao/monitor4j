package cn.jmonitor.monitor4j.websupport;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jmonitor.monitor4j.websupport.collector.ExceptionCollector;
import cn.jmonitor.monitor4j.websupport.collector.JvmCollector;
import cn.jmonitor.monitor4j.websupport.collector.SpringMethodCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebIPCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebProfileCollector;
import cn.jmonitor.monitor4j.websupport.collector.WebUrlCollector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectManager {

    private static Log LOG = LogFactory.getLog(CollectManager.class);

    private static final ScheduledExecutorService minExecutorService = Executors.newScheduledThreadPool(1);

    public synchronized static void startCollect() {

        minExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                JvmCollector.doCollect();
                ExceptionCollector.doCollect();
                SpringMethodCollector.doCollect();
                WebUrlCollector.doCollect();
                WebIPCollector.doCollect();
                WebProfileCollector.doCollect();
                LOG.info("Monitor4j Collect:" + new Date());
            }

        }, 5L, 60L, TimeUnit.SECONDS);
    }
}
