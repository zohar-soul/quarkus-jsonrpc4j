package io.quarkiverse.jsonrpc.runtime;

import java.util.Map;

import io.quarkus.runtime.annotations.*;

@ConfigRoot(name = "json-rpc", phase = ConfigPhase.BOOTSTRAP)
public class JsonRpcConfig {
    /**
     * config list
     */
    @ConfigDocSection
    @ConfigDocMapKey("config-name")
    @ConfigItem(name = ConfigItem.PARENT)
    public Map<String, Config> configs;

    @ConfigGroup
    public static class Config {
        /**
         * baseUrl
         */
        @ConfigItem(defaultValue = "baseUrl")
        public String baseUrl;
        /**
         * scanPackage
         */
        @ConfigItem(defaultValue = "scanPackage")
        public String scanPackage;

    }

}
