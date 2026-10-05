package br.edu.fatec.todo.dto;

import br.edu.fatec.todo.model.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TarefaRequest(
		@NotBlank(message = "O nome da tarefa é obrigatório")
		@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
		String nome,

		@Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
		String descricao,

		StatusTarefa status,

		@Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
		String observacoes) {
}
