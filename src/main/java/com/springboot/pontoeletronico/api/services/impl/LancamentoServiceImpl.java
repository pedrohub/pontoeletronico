package com.springboot.pontoeletronico.api.services.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.springboot.pontoeletronico.api.entities.Lancamento;
import com.springboot.pontoeletronico.api.repositories.LancamentoRepository;
import com.springboot.pontoeletronico.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	
	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Override
	public Optional<Lancamento> buscarPorId(Long id) {
		log.info("buscando por id {}", id);
		return Optional.ofNullable(this.lancamentoRepository.findOne(id));
	}

	@Override
	public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest page) {
		log.info("buscando por funcionario id{}" , funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);
	}

	@Override
	public Lancamento persistir(Lancamento lancamento) {
		log.info("salavando {}" , lancamento);
		return this.lancamentoRepository.save(lancamento);
	}

	@Override
	public void remover(Long id) {
		log.info("removendo {}" , id);
		this.lancamentoRepository.delete(id);
		
	}

	
	
	
}
