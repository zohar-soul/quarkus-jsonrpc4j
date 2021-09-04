//package com.googlecode.jsonrpc4j;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.jboss.resteasy.spi.HttpRequest;
//import org.jboss.resteasy.spi.HttpResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//
//public class MyJsonRprServer extends JsonRpcBasicServer {
//    private static final Logger logger = LoggerFactory.getLogger(MyJsonRprServer.class);
//
//    private static final String GZIP = "gzip";
//    private String contentType = JSONRPC_CONTENT_TYPE;
//
//
//    public MyJsonRprServer(ObjectMapper mapper, Object handler, Class<?> remoteInterface) {
//        super(mapper, handler, remoteInterface);
//    }
//
//    public MyJsonRprServer(ObjectMapper mapper, Object handler) {
//        super(mapper, handler, null);
//    }
//
//    public MyJsonRprServer(Object handler, Class<?> remoteInterface) {
//        super(new ObjectMapper(), handler, remoteInterface);
//    }
//
//    public MyJsonRprServer(Object handler) {
//        super(new ObjectMapper(), handler, null);
//    }
//
//    public void handle(HttpRequest request, HttpResponse response) throws IOException {
//        logger.debug("Handling HttpRequest {}", request);
////        response.setContentType(contentType);
//        OutputStream output = response.getOutputStream();
//        InputStream input = getRequestStream(request);
//        int result = ErrorResolver.JsonError.PARSE_ERROR.code;
//        int contentLength = 0;
//        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
//        try {
//            String acceptEncoding = request.getHttpHeaders().getHeaderString(ACCEPT_ENCODING);
//            result = handleRequest0(input, output, acceptEncoding, response, byteOutput);
//
//            contentLength = byteOutput.size();
//        } catch (Throwable t) {
//            if (StreamEndedException.class.isInstance(t)) {
//                logger.debug("Bad request: empty contents!");
//            } else {
//                logger.error(t.getMessage(), t);
//            }
//        }
//        int httpStatusCode = httpStatusCodeProvider == null ? DefaultHttpStatusCodeProvider.INSTANCE.getHttpStatusCode(result)
//                : httpStatusCodeProvider.getHttpStatusCode(result);
//        response.setStatus(httpStatusCode);
////        response.set(contentLength);
//        byteOutput.writeTo(output);
//        output.flush();
//    }
//
//    private InputStream getRequestStream(HttpRequest request) throws IOException {
//        InputStream input;
//        if (request.getHttpMethod().equals("POST")) {
//            input = request.getInputStream();
//        } else if (request.getHttpMethod().equals("GET")) {
//            input = createInputStream(request);
//        } else {
//            throw new IOException("Invalid request method, only POST and GET is supported");
//        }
//        return input;
//    }
//
//    private int handleRequest0(InputStream input, OutputStream output, String contentEncoding, HttpResponse response, ByteArrayOutputStream byteOutput) throws IOException {
//        return handleRequest(input, byteOutput);
//    }
//
//    private static InputStream createInputStream(HttpRequest request) throws IOException {
//        String method = request.getUri().getQueryParameters().getFirst(METHOD);
//        String id = request.getUri().getQueryParameters().getFirst(ID);
//        String params = request.getUri().getQueryParameters().getFirst(PARAMS);
//        if (method == null && id == null && params == null) {
//            return new ByteArrayInputStream(new byte[]{});
//        } else {
//            return createInputStream(method, id, params);
//        }
//    }
//
//    public void setContentType(String contentType) {
//        this.contentType = contentType;
//    }
//
//
//}
