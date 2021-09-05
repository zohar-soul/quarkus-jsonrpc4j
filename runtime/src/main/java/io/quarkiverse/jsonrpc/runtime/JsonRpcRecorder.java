package io.quarkiverse.jsonrpc.runtime;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JsonRpcRecorder {
    private static final Logger LOG = LoggerFactory.getLogger(JsonRpcRecorder.class);

    public Supplier<Object> jsonRpcSupplier(String dotName, String prefix, JsonRpcConfig jsonRpcConfig, String serviceUrl) {
        Map<String, JsonRpcConfig.Config> configsMap = jsonRpcConfig.configs;
        LOG.info("addJsonRpcServices, start configs");
        if (configsMap == null || configsMap.size() == 0) {
            configsMap = new HashMap<>();
        }
        LOG.info("addJsonRpcServices, configs={}", configsMap.size());
        List<JsonRpcConfig.Config> configs = new ArrayList<>(configsMap.values());
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(t -> t.scanPackage, t -> t.baseUrl));
        String baseUrl = configMap.get(prefix);
        return () -> {
            try {
                LOG.info("start new JsonRpcHttpClient = {}, baseUrl = {}, serviceUrl = {} ", dotName, baseUrl, serviceUrl);
                JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(baseUrl + serviceUrl));
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                LOG.info("start register rpc service name = {}", dotName);
                Object clientProxy = ProxyUtil.createClientProxy(classLoader, Class.forName(dotName, true, classLoader),
                        client);
                LOG.info("end register rpc service name = {}", dotName);
                return clientProxy;
            } catch (Exception e) {
                LOG.error("create rpc service error", e);
                return null;
            }
        };
    }

}
