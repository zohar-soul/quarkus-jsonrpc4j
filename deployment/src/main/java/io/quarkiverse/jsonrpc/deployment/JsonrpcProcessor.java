package io.quarkiverse.jsonrpc.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcService;

import io.quarkiverse.jsonrpc.runtime.JsonRpcConfig;
import io.quarkiverse.jsonrpc.runtime.JsonRpcRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class JsonrpcProcessor {

    private static final String FEATURE = "jsonrpc";
    private static final DotName JSON_RPC_SERVICE = DotName.createSimple(JsonRpcService.class.getName());
    private static final Logger LOG = Logger.getLogger(JsonrpcProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void scanJsonRpcServices(BuildProducer<ReflectiveClassBuildItem> reflective,
            BuildProducer<NativeImageProxyDefinitionBuildItem> proxy,
            BuildProducer<RpcServiceItem> rpcServiceItems,
            CombinedIndexBuildItem indexBuildItem) {
        for (AnnotationInstance i : indexBuildItem.getIndex().getAnnotations(JSON_RPC_SERVICE)) {
            if (i.target().kind() == AnnotationTarget.Kind.CLASS) {
                String serviceUrl = i.value().asString();
                DotName dotName = i.target().asClass().name();
                LOG.infof("rpc service name = %s", dotName.toString());
                reflective.produce(new ReflectiveClassBuildItem(true, false, dotName.toString()));
                proxy.produce(new NativeImageProxyDefinitionBuildItem(dotName.toString()));
                rpcServiceItems.produce(new RpcServiceItem(dotName, serviceUrl));
            }
        }
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void addJsonRpcServices(BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            List<RpcServiceItem> rpcServiceItems,
            JsonRpcConfig jsonRpcConfig,
            JsonRpcRecorder jsonRpcRecorder) {
        LOG.infof("addJsonRpcServices, rpcServiceItems=%s", rpcServiceItems.size());
        List<JsonRpcConfig.Config> configs = new ArrayList<>(jsonRpcConfig.configs.values());
        //        LOG.info("configs = {}", configs);
        if (configs == null || configs.size() == 0) {
            return;
        }
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(t -> t.scanPackage, t -> t.baseUrl));
        for (RpcServiceItem rpcServiceItem : rpcServiceItems) {
            DotName dotName = rpcServiceItem.getServiceName();
            String baseUrl = configMap.get(dotName.prefix().toString());
            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                    .configure(dotName)
                    .scope(Singleton.class)
                    .unremovable()
                    //                    .setRuntimeInit()
                    .supplier(jsonRpcRecorder.jsonRpcSupplier(dotName.toString(), baseUrl, rpcServiceItem.getServiceUrl()));
            syntheticBeanBuildItemBuildProducer.produce(configurator.done());
        }

    }
}
