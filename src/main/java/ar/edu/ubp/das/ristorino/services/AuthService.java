package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.Cliente;
import ar.edu.ubp.das.ristorino.beans.LoginRequest;
import ar.edu.ubp.das.ristorino.beans.LoginResponse;
import ar.edu.ubp.das.ristorino.beans.RegistroRequest;
import ar.edu.ubp.das.ristorino.beans.RegistroResponse;

public interface AuthService {

    // Req 1
    RegistroResponse registrar(RegistroRequest registro);

    // Req 2: devuelve null si las credenciales no son válidas
    LoginResponse login(LoginRequest login);

    // Req 5
    void logout(String token);

    Cliente getPerfil(int nroCliente);
}
