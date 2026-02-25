package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.model.Cierre;
import ar.com.lbr.precisionappbe.repositories.CierreRepository;
import ar.com.lbr.precisionappbe.dto.response.CierreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CierreService {

    private final CierreRepository cierreRepository;

    public CierreService(CierreRepository cierreRepository) {
        this.cierreRepository = cierreRepository;
    }

    public CierreResponse getAllCierres(Pageable pageable) {
        Page<Cierre> cierresPage = cierreRepository.findAll(pageable);

        List<CierreDTO> cierreDTOs = cierresPage.getContent().stream()
                .map(CierreDTO::toDTO)
                .collect(Collectors.toList());

        return new CierreResponse(cierreDTOs, cierresPage.getTotalElements());
    }

    public CierreResponse getAllCierresByMesCierre(String mesCierre, Pageable pageable) {
        Page<Cierre> cierresPage = cierreRepository.findByMesCierre(mesCierre, pageable);

        List<CierreDTO> cierreDTOs = cierresPage.getContent().stream()
                .map(CierreDTO::toDTO)
                .collect(Collectors.toList());

        return new CierreResponse(cierreDTOs, cierresPage.getTotalElements());
    }
}
