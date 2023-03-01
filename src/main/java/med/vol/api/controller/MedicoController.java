package med.vol.api.controller;

import jakarta.validation.Valid;
import med.vol.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired //Injeção de dependencias, auto instanciação feita pelo Spring
    private MedicoRepository repository;

    @PostMapping // Realizar post
    @Transactional // Transação ativa com banco de dados devido a inserção de dados nele
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados) {
        repository.save(new Medico(dados));
        return;
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    /*
        //Exclusão do banco de dados.
        @DeleteMapping("/{id}")// Path parametro na url
        @Transactional
        public void excluir(@PathVariable Long id) {
            repository.deleteById(id);
        }
    */
    //Exclusão lógica, modificando status de ativação do médico
    @DeleteMapping("/{id}")// Path parametro na url
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
        return ResponseEntity.noContent().build();
    }

}
