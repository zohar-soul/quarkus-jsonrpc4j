package io.quarkiverse.jsonrpc.runtime;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JsonRpcRecorder {
    private static final Logger LOG = LoggerFactory.getLogger(JsonRpcRecorder.class);

    public Supplier<Object> jsonRpcSupplier(String dotName, String prefix, JsonRpcConfig jsonRpcConfig, String serviceUrl) {
        Map<String, JsonRpcConfig.Config> configsMap = jsonRpcConfig.configs;
        if (configsMap == null || configsMap.size() == 0) {
            configsMap = new HashMap<>();
        }
        LOG.info("addJsonRpcServices, configs={}", configsMap.size());
        List<JsonRpcConfig.Config> configs = new ArrayList<>(configsMap.values());
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(t -> t.scanPackage, t -> t.baseUrl));
        String baseUrl = configMap.get(prefix);
        LOG.info("addJsonRpcServices, configs={}", configsMap.size());
        return () -> {
            try {
                LOG.info("start new JsonRpcHttpClient = {}, baseUrl = {}, serviceUrl = {} ", dotName, baseUrl, serviceUrl);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                JsonRpcHttpClient client = new JsonRpcHttpClient(objectMapper, new URL(baseUrl + serviceUrl), new HashMap<>());
                client.setConnectionTimeoutMillis(3000);
                client.setReadTimeoutMillis(3000);
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
