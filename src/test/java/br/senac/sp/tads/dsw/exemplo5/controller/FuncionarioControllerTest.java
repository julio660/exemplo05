package br.senac.sp.tads.dsw.exemplo5.controller;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FuncionarioControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private FuncionarioRepository funcionarioRepository;

  @Autowired
  private DepartamentoRepository departamentoRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Departamento departamentoPadrao;

  @BeforeEach
  void setUp() {
    Departamento depto = new Departamento();
    depto.setNome("Desenvolvimento");
    depto.setOrcamento(200000.0);
    departamentoPadrao = departamentoRepository.save(depto);
  }

  @Test
  void deveCriarFuncionarioComSucesso() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Ana Souza");
    funcionario.setDataContratacao(LocalDate.now());
    funcionario.setTrabalhoRemoto(true);
    funcionario.setDepartamento(departamentoPadrao);

    String jsonRequisicao = objectMapper.writeValueAsString(funcionario);

    mockMvc.perform(post("/api/funcionarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequisicao))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.nome").value("Ana Souza"));
  }

  @Test
  void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Carlos Silva");
    funcionario.setDataContratacao(LocalDate.now().plusDays(10));
    funcionario.setTrabalhoRemoto(false);
    funcionario.setDepartamento(departamentoPadrao);

    String jsonRequisicao = objectMapper.writeValueAsString(funcionario);

    mockMvc.perform(post("/api/funcionarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequisicao))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deveListarTodosOsFuncionarios() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Beatriz");
    funcionario.setDataContratacao(LocalDate.now());
    funcionario.setTrabalhoRemoto(true);
    funcionario.setDepartamento(departamentoPadrao);
    funcionarioRepository.save(funcionario);

    mockMvc.perform(get("/api/funcionarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Beatriz"));
  }

  @Test
  void deveBuscarFuncionarioPorId() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Daniel");
    funcionario.setDataContratacao(LocalDate.now());
    funcionario.setTrabalhoRemoto(false);
    funcionario.setDepartamento(departamentoPadrao);
    Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

    mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Daniel"));
  }

  @Test
  void deveAtualizarFuncionario() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Eduardo");
    funcionario.setDataContratacao(LocalDate.now());
    funcionario.setTrabalhoRemoto(false);
    funcionario.setDepartamento(departamentoPadrao);
    Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

    funcionarioSalvo.setTrabalhoRemoto(true);
    String jsonAtualizado = objectMapper.writeValueAsString(funcionarioSalvo);

    mockMvc.perform(put("/api/funcionarios/" + funcionarioSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonAtualizado))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.trabalhoRemoto").value(true));
  }

  @Test
  void deveApagarFuncionario() throws Exception {
    Funcionario funcionario = new Funcionario();
    funcionario.setNome("Fernanda");
    funcionario.setDataContratacao(LocalDate.now());
    funcionario.setTrabalhoRemoto(true);
    funcionario.setDepartamento(departamentoPadrao);
    Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

    mockMvc.perform(delete("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isNotFound());
  }
}
