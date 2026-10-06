package br.senac.sp.tads.dsw.exemplo5.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.jackson.databind.ObjectMapper;
import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class DepartamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarDepartamentoComSucesso() throws Exception {
        // 1. PREPARAÇÃO
        Departamento depto = new Departamento();
        depto.setNome("Turma B - TADS");
        depto.setOrcamento(3600.00);

        String jsonRequisicao = objectMapper.writeValueAsString(depto);

        // 2. AÇÃO
        mockMvc.perform(post("/api/departamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequisicao))

                // 3. VERIFICAÇÃO
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Turma B - TADS"))
                .andExpect(jsonPath("$.orcamento").value(3600.00));
    }

    @Test
    void deveListarTodosOsDepartamentos() throws Exception {
        Departamento depto = new Departamento();
        depto.setNome("RH");
        depto.setOrcamento(3600.0);
        repository.save(depto);

        mockMvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("RH"));
    }

    @Test
    void deveBuscarDepartamentoPorId() throws Exception {
        Departamento depto = new Departamento();
        depto.setNome("Marketing");
        depto.setOrcamento(80000.0);
        Departamento deptoSalvo = repository.save(depto);

        mockMvc.perform(get("/api/departamentos/" + deptoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Marketing"));
    }

    @Test
    void deveAtualizarDepartamento() throws Exception {
        Departamento deptoSalvo = new Departamento();
        deptoSalvo.setNome("Financeiro");
        deptoSalvo.setOrcamento(100.0);
        deptoSalvo = repository.save(deptoSalvo);

        deptoSalvo.setOrcamento(500000.0);
        String jsonAtualizado = objectMapper.writeValueAsString(deptoSalvo);

        mockMvc.perform(put("/api/departamentos/" + deptoSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orcamento").value(500000.0));
    }

    @Test
    void deveApagarDepartamento() throws Exception {
        Departamento depto = new Departamento();
        depto.setNome("Limpeza");
        depto.setOrcamento(1000.0);
        Departamento deptoSalvo = repository.save(depto);

        mockMvc.perform(delete("/api/departamentos/" + deptoSalvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/departamentos/" + deptoSalvo.getId()))
                .andExpect(status().isNotFound());
    }

}
