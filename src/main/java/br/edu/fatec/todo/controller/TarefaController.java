package br.edu.fatec.todo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatec.todo.dto.TarefaRequest;
import br.edu.fatec.todo.model.StatusTarefa;
import br.edu.fatec.todo.model.Tarefa;
import br.edu.fatec.todo.service.TarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

	private final TarefaService service;

	public TarefaController(TarefaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Tarefa> criar(@Valid @RequestBody TarefaRequest dados) {
		Tarefa tarefa = service.criar(dados);
		return ResponseEntity.created(URI.create("/api/tarefas/" + tarefa.getId())).body(tarefa);
	}

	@GetMapping
	public List<Tarefa> listar(@RequestParam(required = false) StatusTarefa status) {
		return service.listar(status);
	}

	@GetMapping("/{id}")
	public Tarefa buscarPorId(@PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public Tarefa alterar(@PathVariable Long id, @Valid @RequestBody TarefaRequest dados) {
		return service.alterar(id, dados);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		service.deletar(id);
		return ResponseEntity.noContent().build();
	}

}
