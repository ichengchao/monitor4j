package cn.jmonitor.monitor4j.client.protocal.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jmonitor.monitor4j.common.JmonitorConstants;

public class Heartbeat extends BaseMessage {

    @Override
    public String getType() {
        return JmonitorConstants.MSG_HEARTBEAT;
    }

    @Override
    public Map<String, Object> getBody() {
        Map<String, Object> map = new HashMap<String, Object>();
        Date now = new Date();
        map.put(JmonitorConstants.MSG_TS, now.getTime());
        return map;
    }

    public static void main(String[] args) {
        Heartbeat heartbeat = new Heartbeat();
        System.out.println(heartbeat.buildMsg());
    }

    @Override
    public boolean isRequestFormat() {
        return true;
    }

}
