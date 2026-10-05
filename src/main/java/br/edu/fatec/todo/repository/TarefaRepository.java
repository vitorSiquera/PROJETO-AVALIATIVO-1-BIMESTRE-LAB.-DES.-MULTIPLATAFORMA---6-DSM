package br.edu.fatec.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fatec.todo.model.StatusTarefa;
import br.edu.fatec.todo.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

	List<Tarefa> findByStatus(StatusTarefa status);

}
