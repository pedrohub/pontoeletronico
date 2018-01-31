package com.springboot.pontoeletronico.api.services.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.pontoeletronico.api.entities.Funcionario;
import com.springboot.pontoeletronico.api.repositories.FuncionarioRepository;
import com.springboot.pontoeletronico.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService{

	private static final Logger log = LoggerFactory.getLogger(FuncionarioServiceImpl.class);
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Override
	public Optional<Funcionario> buscaPorCpf(String cnpj) {
		log.info("Buscar por CPF {}", cnpj);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cnpj));
	}

	@Override
	public Funcionario persistir(Funcionario funcionario) {
		log.info("Salvando {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}

	@Override
	public Optional<Funcionario> buscaPorId(Long id) {
		log.info("Buscar por id {}", id);
		return Optional.ofNullable(this.funcionarioRepository.findOne(id));
	}

}
