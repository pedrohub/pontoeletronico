package com.springboot.pontoeletronico.api.services;

import java.util.Optional;

import com.springboot.pontoeletronico.api.entities.Empresa;

public interface EmpresaService {
	
	/**
	 * Retorna uma empresa
	 * 
	 * @param cnpj
	 * @return
	 */
	Optional<Empresa> buscaPorCnpj(String cnpj);
	
	/**
	 * Cadastra uma empresa
	 * 
	 * @param empresa
	 * @return
	 */
	Empresa persistir(Empresa empresa);

}
