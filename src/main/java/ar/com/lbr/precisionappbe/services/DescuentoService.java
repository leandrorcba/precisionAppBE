package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;

public class DescuentoService {

    private final DescuentoRepository descuentoRepository;

    public DescuentoService(DescuentoRepository descuentoRepository) {
        this.descuentoRepository = descuentoRepository;
    }

    
}
