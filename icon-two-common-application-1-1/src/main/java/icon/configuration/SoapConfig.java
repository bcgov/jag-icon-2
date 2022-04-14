package icon.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.HashMap;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;

@EnableWs
@Configuration
@Slf4j
public class SoapConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        return restTemplate;
    }

    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return objectMapper;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        webServiceTemplate.setMessageFactory(messageFactory());
        jaxb2Marshaller.setContextPaths(
                "ca.bc.gov.open.adobe.ws",
                "ca.bc.gov.open.adobe.scp",
                "ca.bc.gov.open.adobe.diagnostic");
        webServiceTemplate.setMarshaller(jaxb2Marshaller);
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
        webServiceTemplate.afterPropertiesSet();
        return webServiceTemplate;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(SOAPMessage.WRITE_XML_DECLARATION, "true");
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setMessageProperties(props);
        messageFactory.setSoapVersion(SoapVersion.SOAP_11);
        return messageFactory;
    }

    @Bean(name = "ICON2.Source.Audit.ws.provider:Audit")
    public Wsdl11Definition auditWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/Audit.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.ClientServices.ws.provider:MyInfo")
    public Wsdl11Definition ClientServicesWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/ClientServices.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.EReporting.ws.provider:EReporting")
    public Wsdl11Definition eReportingWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/EReporting.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.Common.ws.provider:ErrorHandling")
    public Wsdl11Definition errorWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/ErrorHandling.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.HealthServiceRequest.ws.provider:HSR")
    public Wsdl11Definition HSRWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/HSR.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.Message.ws.provider:Message")
    public Wsdl11Definition MessageWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/Message.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.MyInfo.ws.provider:MyInfo")
    public Wsdl11Definition MyInfoWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/MyInfo.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.Version.ws.provider:PackageInfo")
    public Wsdl11Definition PackageInfoWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/PackageInfo.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.Common.ws.provider:SessionParameter")
    public Wsdl11Definition SessionWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/SessionParameter.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.TombStoneInfo.ws.provider:TombStoneInfo")
    public Wsdl11Definition TombStoneWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/TomStoneInfo.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.TrustAccount.ws.provider:TrustAccount")
    public Wsdl11Definition TrustAccountWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/TrustAccount.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "ICON2.Source.VisitSchedule.ws.provider:VisitSchedule")
    public Wsdl11Definition VisitScheduleWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("wsdl/VisitSchedule.wsdl"));
        return wsdl11Definition;
    }
}
