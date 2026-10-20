package ar.edu.ubp.das.ristorino.components;

import ar.edu.ubp.das.ristorino.utils.SOAPClient;
import org.springframework.stereotype.Component;

// Crea el cliente SOAP dinámico de la cátedra (SOAPClient con JAX-WS Dispatch + WS-Security UsernameToken)
@Component
public class SOAPClientFactory {
    public SOAPClient createSOAPClient(String wsdlUrl, String namespace, String serviceName, String portName,
                                       String operationName, String username, String password) {
        return new SOAPClient.SOAPClientBuilder()
                .wsdlUrl(wsdlUrl)
                .namespace(namespace)
                .serviceName(serviceName)
                .portName(portName)
                .operationName(operationName)
                .username(username)
                .password(password)
                .build();
    }
}
