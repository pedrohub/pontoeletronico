package com.springboot.pontoeletronico.api.services;

import java.util.Optional;

import com.springboot.pontoeletronico.api.entities.Funcionario;

public interface FuncionarioService {

	/**
	 * Busca funcionario por cpf
	 * 
	 * @param cnpj
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscaPorCpf(String cnpj);
	
	/**
	 * Salva Funcionario
	 * 
	 * @param funcionario
	 * @return Empresa persistir
	 */
	Funcionario persistir(Funcionario funcionario);
	
	/**
	 * Buscar funcionario por ID
	 * 
	 * @param id
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscaPorId(Long id);
}
