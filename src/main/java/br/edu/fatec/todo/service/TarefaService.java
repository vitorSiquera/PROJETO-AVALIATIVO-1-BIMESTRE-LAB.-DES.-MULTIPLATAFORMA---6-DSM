package br.edu.fatec.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fatec.todo.dto.TarefaRequest;
import br.edu.fatec.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatec.todo.model.StatusTarefa;
import br.edu.fatec.todo.model.Tarefa;
import br.edu.fatec.todo.repository.TarefaRepository;

@Service
public class TarefaService {

	private final TarefaRepository repository;

	public TarefaService(TarefaRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Tarefa criar(TarefaRequest dados) {
		Tarefa tarefa = new Tarefa(dados.nome(), dados.descricao(), dados.status(), dados.observacoes());
		return repository.save(tarefa);
	}

	@Transactional(readOnly = true)
	public List<Tarefa> listar(StatusTarefa status) {
		return status == null ? repository.findAll() : repository.findByStatus(status);
	}

	@Transactional(readOnly = true)
	public Tarefa buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new TarefaNaoEncontradaException(id));
	}

	@Transactional
	public Tarefa alterar(Long id, TarefaRequest dados) {
		Tarefa tarefa = buscarPorId(id);
		tarefa.setNome(dados.nome());
		tarefa.setDescricao(dados.descricao());
		tarefa.setObservacoes(dados.observacoes());
		if (dados.status() != null) {
			tarefa.setStatus(dados.status());
		}
		return repository.saveAndFlush(tarefa);
	}

	@Transactional
	public void deletar(Long id) {
		repository.delete(buscarPorId(id));
	}

}
