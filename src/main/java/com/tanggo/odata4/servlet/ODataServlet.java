package com.tanggo.odata4.servlet;

import com.tanggo.odata4.processor.OrderEntityCollectionProcessor;
import com.tanggo.odata4.processor.OrderEntityProcessor;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.stereotype.Component;

@Component
public class ODataServlet extends HttpServlet {
    private final OData odata;
    private final ServiceMetadata serviceMetadata;
    private final OrderEntityProcessor entityProcessor;
    private final OrderEntityCollectionProcessor entityCollectionProcessor;

    public ODataServlet(OData odata,
                       ServiceMetadata serviceMetadata,
                       OrderEntityProcessor entityProcessor,
                       OrderEntityCollectionProcessor entityCollectionProcessor) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
        this.entityProcessor = entityProcessor;
        this.entityCollectionProcessor = entityCollectionProcessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {
        try {
            // 创建处理器
            ODataHttpHandler handler = odata.createHandler(serviceMetadata);

            // 注册处理器
            handler.register(entityProcessor);
            handler.register(entityCollectionProcessor);

            // 处理请求
            handler.process(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
