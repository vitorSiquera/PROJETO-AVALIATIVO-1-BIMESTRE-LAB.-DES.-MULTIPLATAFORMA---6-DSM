package br.edu.fatec.todo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.fatec.todo.model.StatusTarefa;
import br.edu.fatec.todo.model.Tarefa;
import br.edu.fatec.todo.repository.TarefaRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TarefaControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TarefaRepository repository;

	@BeforeEach
	void limparBanco() {
		repository.deleteAll();
	}

	@Test
	void deveCriarTarefa() throws Exception {
		String json = """
				{
				  "nome": "Estudar Spring Boot",
				  "descricao": "Revisar JPA e REST",
				  "observacoes": "Foco em testes"
				}
				""";

		mockMvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.nome").value("Estudar Spring Boot"))
				.andExpect(jsonPath("$.descricao").value("Revisar JPA e REST"))
				.andExpect(jsonPath("$.status").value("PENDENTE"))
				.andExpect(jsonPath("$.observacoes").value("Foco em testes"))
				.andExpect(jsonPath("$.dataCriacao", notNullValue()))
				.andExpect(jsonPath("$.dataAtualizacao", notNullValue()));

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void naoDeveCriarTarefaSemNome() throws Exception {
		mockMvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\": \"  \", \"descricao\": \"Sem nome\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos.nome", notNullValue()));

		assertThat(repository.count()).isZero();
	}

	@Test
	void naoDeveCriarTarefaComStatusInvalido() throws Exception {
		mockMvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\": \"Tarefa\", \"status\": \"INEXISTENTE\"}"))
				.andExpect(status().isBadRequest());

		assertThat(repository.count()).isZero();
	}

	@Test
	void deveListarTarefasEFiltrarPorStatus() throws Exception {
		repository.save(new Tarefa("Tarefa 1", null, StatusTarefa.PENDENTE, null));
		repository.save(new Tarefa("Tarefa 2", null, StatusTarefa.CONCLUIDA, null));

		mockMvc.perform(get("/api/tarefas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));

		mockMvc.perform(get("/api/tarefas").param("status", "CONCLUIDA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nome").value("Tarefa 2"));
	}

	@Test
	void deveBuscarTarefaPorId() throws Exception {
		Tarefa salva = repository.save(new Tarefa("Tarefa", "Descrição", StatusTarefa.PENDENTE, null));

		mockMvc.perform(get("/api/tarefas/{id}", salva.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(salva.getId()))
				.andExpect(jsonPath("$.nome").value("Tarefa"));
	}

	@Test
	void deveRetornar404AoBuscarTarefaInexistente() throws Exception {
		mockMvc.perform(get("/api/tarefas/{id}", 9999))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.erro", notNullValue()));
	}

	@Test
	void deveAlterarTarefa() throws Exception {
		Tarefa salva = repository.save(new Tarefa("Antigo", "Descrição antiga", StatusTarefa.PENDENTE, null));
		String json = """
				{
				  "nome": "Novo",
				  "descricao": "Descrição nova",
				  "status": "CONCLUIDA",
				  "observacoes": "Finalizada"
				}
				""";

		mockMvc.perform(put("/api/tarefas/{id}", salva.getId()).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Novo"))
				.andExpect(jsonPath("$.descricao").value("Descrição nova"))
				.andExpect(jsonPath("$.status").value("CONCLUIDA"))
				.andExpect(jsonPath("$.observacoes").value("Finalizada"));

		Tarefa alterada = repository.findById(salva.getId()).orElseThrow();
		assertThat(alterada.getNome()).isEqualTo("Novo");
		assertThat(alterada.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
		assertThat(alterada.getDataAtualizacao()).isAfterOrEqualTo(alterada.getDataCriacao());
	}

	@Test
	void deveRetornar404AoAlterarTarefaInexistente() throws Exception {
		mockMvc.perform(put("/api/tarefas/{id}", 9999).contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\": \"Novo\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deveDeletarTarefa() throws Exception {
		Tarefa salva = repository.save(new Tarefa("Tarefa", null, StatusTarefa.PENDENTE, null));

		mockMvc.perform(delete("/api/tarefas/{id}", salva.getId()))
				.andExpect(status().isNoContent());

		assertThat(repository.existsById(salva.getId())).isFalse();
	}

	@Test
	void deveRetornar404AoDeletarTarefaInexistente() throws Exception {
		mockMvc.perform(delete("/api/tarefas/{id}", 9999))
				.andExpect(status().isNotFound());
	}

}
