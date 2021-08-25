///*
// * Copyright 2013 Alibaba.com All right reserved. This software is the
// * confidential and proprietary information of Alibaba.com ("Confidential
// * Information"). You shall not disclose such Confidential Information and shall
// * use it only in accordance with the terms of the license agreement you entered
// * into with Alibaba.com.
// */
//package cn.jmonitor.monitor4j.utils;
//
//import java.lang.reflect.Method;
//
//import org.aopalliance.intercept.MethodInvocation;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.aop.TargetSource;
//
//import cn.jmonitor.monitor4j.plugin.spring.SpringMethodItemKey;
//
///**
// * @author charles 2013年12月25日 下午2:23:05
// */
//public class SpringMethodUtils {
//
//    private static Log LOG = LogFactory.getLog(SpringMethodUtils.class);
//
//    public static SpringMethodItemKey getMethodInfo(MethodInvocation invocation) {
//        Object thisObject = invocation.getThis();
//        Method method = invocation.getMethod();
//
//        Class<?> clazz = null;
//
//        try {
//            if (thisObject == null) {
//                clazz = method.getDeclaringClass();
//            } else {
//                // 最多支持10层代理
//                for (int i = 0; i < 10; ++i) {
//                    if (thisObject instanceof org.springframework.aop.framework.Advised) {
//                        TargetSource targetSource = ((org.springframework.aop.framework.Advised) thisObject)
//                                .getTargetSource();
//
//                        if (targetSource == null) {
//                            break;
//                        }
//
//                        Object target = targetSource.getTarget();
//                        if (target != null) {
//                            thisObject = target;
//                        } else {
//                            clazz = targetSource.getTargetClass();
//                            break;
//                        }
//                    } else {
//                        break;
//                    }
//                }
//
//                if (clazz == null) {
//                    clazz = thisObject.getClass();
//                    if (clazz.getName().startsWith("$")) {
//                        clazz = method.getDeclaringClass();
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            LOG.error(ex.getMessage(), ex);
//        }
//
//        if (clazz == null) {
//            clazz = method.getDeclaringClass();
//        }
//
//        return new SpringMethodItemKey(clazz.getName(), getMethodSignature(method));
//    }
//
//    public static String getMethodSignature(Method method) {
//        StringBuffer sb = new StringBuffer();
//
//        sb.append(method.getName() + "(");
//        Class<?>[] params = method.getParameterTypes();
//        for (int j = 0; j < params.length; j++) {
//            sb.append(params[j].getName());
//            if (j < (params.length - 1))
//                sb.append(",");
//        }
//        sb.append(")");
//        return sb.toString();
//    }
//
//}
