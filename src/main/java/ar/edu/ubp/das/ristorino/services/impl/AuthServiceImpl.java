package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.Cliente;
import ar.edu.ubp.das.ristorino.beans.LoginRequest;
import ar.edu.ubp.das.ristorino.beans.LoginResponse;
import ar.edu.ubp.das.ristorino.beans.RegistroRequest;
import ar.edu.ubp.das.ristorino.beans.RegistroResponse;
import ar.edu.ubp.das.ristorino.components.JwtTokenProvider;
import ar.edu.ubp.das.ristorino.repository.ClienteRepository;
import ar.edu.ubp.das.ristorino.services.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final ClienteRepository clienteRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(ClienteRepository clienteRepository, JwtTokenProvider jwtTokenProvider) {
        this.clienteRepository = clienteRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public RegistroResponse registrar(RegistroRequest registro) {
        if (registro.getClave() == null || !registro.getClave().equals(registro.getConfirmacionClave())) {
            RegistroResponse respuesta = new RegistroResponse();
            respuesta.setEstado("RECHAZADO");
            respuesta.setMotivo("CLAVES_NO_COINCIDEN");
            return respuesta;
        }
        // La contraseña se guarda hasheada (BCrypt), nunca en texto plano
        return clienteRepository.insCliente(registro, passwordEncoder.encode(registro.getClave()));
    }

    @Override
    public LoginResponse login(LoginRequest login) {
        Cliente cliente = clienteRepository.findByCorreo(login.getCorreo());
        if (cliente == null || !cliente.isHabilitado() || !passwordEncoder.matches(login.getClave(), cliente.getClave())) {
            return null;
        }

        LoginResponse respuesta = new LoginResponse();
        respuesta.setToken(jwtTokenProvider.generarToken(cliente.getNroCliente(), cliente.getCorreo()));
        respuesta.setNroCliente(cliente.getNroCliente());
        respuesta.setApellido(cliente.getApellido());
        respuesta.setNombre(cliente.getNombre());
        respuesta.setCorreo(cliente.getCorreo());
        return respuesta;
    }

    @Override
    public void logout(String token) {
        jwtTokenProvider.revocarToken(token);
    }

    @Override
    public Cliente getPerfil(int nroCliente) {
        return clienteRepository.getCliente(nroCliente);
    }
}
