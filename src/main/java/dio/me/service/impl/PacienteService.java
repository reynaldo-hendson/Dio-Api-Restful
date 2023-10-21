package dio.me.service.impl;

import dio.me.domain.model.Paciente;
import dio.me.domain.repository.PacienteRepository;
import dio.me.exception.NegocioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class PacienteService {

    public final PacienteRepository repository;

    public Paciente salvar(Paciente paciente){
        boolean existeCpf = false;

        Optional<Paciente> optionalPaciente = repository.findByCpf(paciente.getCpf());

        if(optionalPaciente.isPresent()){
            if(!optionalPaciente.get().getId().equals(paciente.getId())){
                existeCpf = true;
            }
        }
        if(existeCpf){
            throw new NegocioException("Cpf já cadastrado.");
        }
        if(repository.existsByEmail(paciente.getEmail())){
            throw new NegocioException("Email já cadastrado.");
        }
        return repository.save(paciente);
    }

    public Paciente alterar(Long id, Paciente paciente) {
        Optional<Paciente> optPaciente = this.buscarPorId(id);

        if(optPaciente.isPresent()) {
            Paciente pacienteExistente = optPaciente.get();
            pacienteExistente.setNome(paciente.getNome());
            pacienteExistente.setSobrenome(paciente.getSobrenome());
            pacienteExistente.setCpf(paciente.getCpf());
            pacienteExistente.setEmail(paciente.getEmail());
            pacienteExistente.setTelefone(paciente.getTelefone());

            return repository.save(pacienteExistente);
        }else{
            throw new NegocioException("Paciente com ID: "+id+" não cadastrado!");
        }

        //paciente.setId(id);

    }

    public List<Paciente> findAll(){
        log.info("Buascando todos os pacientes.");
        return repository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id){
        log.info("buscarPorId: {}", id);
        return repository.findById(id);

    }

    public void delete(Long id){
        log.info("delete: {}", id);
        repository.deleteById(id);

    }

//    public List<Paciente> buscarPorNome(String nome){
//        log.info("buscarPorNome: {}", nome);
//        return repository.findByNome(nome);
//    }

}
