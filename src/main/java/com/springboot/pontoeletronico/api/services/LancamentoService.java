package com.springboot.pontoeletronico.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.springboot.pontoeletronico.api.entities.Lancamento;

public interface LancamentoService {
	
	/**
	 * Retorna um lancamento por id
	 * 
	 * @param id
	 * @return
	 */
	Optional<Lancamento> buscarPorId(Long id);
	
	/**
	 * Buscar por funcionarioId
	 * 
	 * @param funcionarioId
	 * @param page
	 * @return
	 */
	Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest page);

	/**
	 * Salvar o lancamento
	 * @param lancamento
	 * @return
	 */
	Lancamento persistir(Lancamento lancamento);
	
	/**
	 * remove um lancamento
	 * @param id
	 */
	void remover(Long id);
	
}
