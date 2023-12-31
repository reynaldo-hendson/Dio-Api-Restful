package dio.me.service;


import dio.me.domain.model.Paciente;
import dio.me.domain.model.Relatorio;
import dio.me.domain.repository.RelatorioRepository;
import dio.me.exception.EntidadeNaoEncontradaException;
import dio.me.exception.NegocioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RelatorioService {

    private final RelatorioRepository repository;
    private final PacienteService pacienteService;

    public Relatorio salvar(Relatorio relatorio) {

        //Valida se paciente existe
        Optional<Paciente> optPaciente = pacienteService.buscarPorId(relatorio.getPaciente().getId());
        if (optPaciente.isEmpty()) {
            log.error("Paciente não encontrado. Lançando EntidadeNaoEncontradaException.");
            throw new EntidadeNaoEncontradaException("Paciente não encontrado.");
        }

        relatorio.setPaciente(optPaciente.get());
        relatorio.setDataCriacao(LocalDateTime.now());
        log.info("Salvando o relatório.");

        return repository.save(relatorio);
    }

    public Optional<Relatorio> buscarPorId(Long id) {

        Optional<Relatorio> optionalRelatorios = repository.findById(id);

        if(optionalRelatorios.isPresent()){
            log.info("buscando relatório por Id: {}", id);
            return repository.findById(id);
        }else{
            throw new EntidadeNaoEncontradaException("Relatório com ID: "+id+" não encontrado.");
        }
    }

    public List<Relatorio> listarRelatoriosPorIdPaciente(Long id) {

        //Valida se paciente existe
        Optional<Paciente> optionalPaciente = pacienteService.buscarPorId(id);
        if (optionalPaciente.isEmpty()) {
            log.error("Paciente não encontrado. Lançando NegocioException.");
            throw new NegocioException("Paciente não encontrado.");
        }

        return repository.findByRelatorios(optionalPaciente.get().getId());

    }

    public List<Relatorio> buscarRelatoriosPorNomePaciente(String nomePaciente) {
        List<Relatorio> relatorios = repository.findByNomePaciente(nomePaciente);

        // Usando Stream API para filtrar a lista
        List<Relatorio> relatoriosFiltrados = relatorios.stream()
                .filter(relatorio -> relatorio.getPaciente().getNome().equals(nomePaciente))
                .toList();
        return relatoriosFiltrados;

    }
}
