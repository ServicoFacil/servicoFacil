package br.com.servicoFacil.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

public class ServicoFacilException extends Exception{
    @Getter
    private ServicoFacilError error;

    @Getter
    private Object[] params;

    @Getter
    private ErrorResponse propagatedError;

    public ServicoFacilException(String message) {
        super(message);
        this.error = ServicoFacilError.SF9999;
    }

    public ServicoFacilException(String message, ServicoFacilError servicoFacilError){
        super(message);
        this.error = servicoFacilError;
    }


    public ServicoFacilException(Exception e, ServicoFacilError servicoFacilError) {
        super(e);
        this.error = servicoFacilError;
    }

    public ServicoFacilException(ServicoFacilError servicoFacilError) {
    }
}