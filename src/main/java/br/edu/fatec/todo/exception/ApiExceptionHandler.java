package br.edu.fatec.todo.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(TarefaNaoEncontradaException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, Object> tarefaNaoEncontrada(TarefaNaoEncontradaException ex) {
		return Map.of("erro", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> dadosInvalidos(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));
		return Map.of("erro", "Dados inválidos", "campos", campos);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> requisicaoInvalida(Exception ex) {
		return Map.of("erro", "Requisição inválida: verifique o JSON enviado. Status aceitos: PENDENTE, EM_ANDAMENTO, CONCLUIDA");
	}

}
