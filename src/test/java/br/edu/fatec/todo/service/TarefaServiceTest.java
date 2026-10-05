package br.edu.fatec.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.fatec.todo.dto.TarefaRequest;
import br.edu.fatec.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatec.todo.model.StatusTarefa;
import br.edu.fatec.todo.model.Tarefa;
import br.edu.fatec.todo.repository.TarefaRepository;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

	@Mock
	private TarefaRepository repository;

	@InjectMocks
	private TarefaService service;

	@Test
	void deveCriarTarefaComStatusPendentePorPadrao() {
		when(repository.save(any(Tarefa.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Tarefa tarefa = service.criar(new TarefaRequest("Estudar", "Revisar JPA", null, "Sem observações"));

		assertThat(tarefa.getNome()).isEqualTo("Estudar");
		assertThat(tarefa.getDescricao()).isEqualTo("Revisar JPA");
		assertThat(tarefa.getObservacoes()).isEqualTo("Sem observações");
		assertThat(tarefa.getStatus()).isEqualTo(StatusTarefa.PENDENTE);
	}

	@Test
	void deveCriarTarefaComStatusInformado() {
		when(repository.save(any(Tarefa.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Tarefa tarefa = service.criar(new TarefaRequest("Estudar", null, StatusTarefa.EM_ANDAMENTO, null));

		assertThat(tarefa.getStatus()).isEqualTo(StatusTarefa.EM_ANDAMENTO);
	}

	@Test
	void deveListarTodasAsTarefasQuandoStatusNaoInformado() {
		when(repository.findAll()).thenReturn(List.of(new Tarefa(), new Tarefa()));

		assertThat(service.listar(null)).hasSize(2);
		verify(repository, never()).findByStatus(any());
	}

	@Test
	void deveListarTarefasFiltrandoPorStatus() {
		when(repository.findByStatus(StatusTarefa.CONCLUIDA)).thenReturn(List.of(new Tarefa()));

		assertThat(service.listar(StatusTarefa.CONCLUIDA)).hasSize(1);
		verify(repository, never()).findAll();
	}

	@Test
	void deveAlterarTarefaExistente() {
		Tarefa existente = new Tarefa("Antigo", "Descrição antiga", StatusTarefa.PENDENTE, null);
		when(repository.findById(1L)).thenReturn(Optional.of(existente));
		when(repository.saveAndFlush(existente)).thenReturn(existente);

		Tarefa alterada = service.alterar(1L,
				new TarefaRequest("Novo", "Descrição nova", StatusTarefa.CONCLUIDA, "Finalizada"));

		assertThat(alterada.getNome()).isEqualTo("Novo");
		assertThat(alterada.getDescricao()).isEqualTo("Descrição nova");
		assertThat(alterada.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
		assertThat(alterada.getObservacoes()).isEqualTo("Finalizada");
	}

	@Test
	void deveManterStatusAtualQuandoAlteracaoNaoInformaStatus() {
		Tarefa existente = new Tarefa("Antigo", null, StatusTarefa.EM_ANDAMENTO, null);
		when(repository.findById(1L)).thenReturn(Optional.of(existente));
		when(repository.saveAndFlush(existente)).thenReturn(existente);

		Tarefa alterada = service.alterar(1L, new TarefaRequest("Novo", null, null, null));

		assertThat(alterada.getStatus()).isEqualTo(StatusTarefa.EM_ANDAMENTO);
	}

	@Test
	void deveLancarExcecaoAoAlterarTarefaInexistente() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.alterar(99L, new TarefaRequest("Novo", null, null, null)))
				.isInstanceOf(TarefaNaoEncontradaException.class)
				.hasMessageContaining("99");
		verify(repository, never()).saveAndFlush(any());
	}

	@Test
	void deveDeletarTarefaExistente() {
		Tarefa existente = new Tarefa("Tarefa", null, StatusTarefa.PENDENTE, null);
		when(repository.findById(1L)).thenReturn(Optional.of(existente));

		service.deletar(1L);

		verify(repository).delete(existente);
	}

	@Test
	void deveLancarExcecaoAoDeletarTarefaInexistente() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.deletar(99L)).isInstanceOf(TarefaNaoEncontradaException.class);
		verify(repository, never()).delete(any());
	}

}
