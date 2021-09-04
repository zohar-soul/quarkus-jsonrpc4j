package io.quarkiverse.jsonrpc.deployment;

import org.jboss.jandex.DotName;

import io.quarkus.builder.item.MultiBuildItem;

public final class RpcServiceItem extends MultiBuildItem {

    private final DotName serviceName;
    private final String serviceUrl;

    public RpcServiceItem(DotName serviceName, String serviceUrl) {
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
    }

    public DotName getServiceName() {
        return serviceName;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }
}
