package com.springboot.pontoeletronico.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.springboot.pontoeletronico.api.entities.Lancamento;
import com.springboot.pontoeletronico.api.repositories.LancamentoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@MockBean
	LancamentoRepository lancametnoRepository;
	
	@Autowired
	LancamentoService lancamentoService;
	
	@Before
	public void setUp() {
		BDDMockito.given(this.lancametnoRepository.findByFuncionarioId(Mockito.anyLong(), Mockito.any(PageRequest.class)))
					.willReturn(new PageImpl<Lancamento>(new ArrayList<Lancamento>()));
		
		BDDMockito.given(this.lancametnoRepository.findOne(Mockito.anyLong())).willReturn(new Lancamento());
		BDDMockito.given(this.lancametnoRepository.save(Mockito.any(Lancamento.class))).willReturn(new Lancamento());
	}
	
	public void testBuscarPorfuncionario() {
		Page<Lancamento> lancamento = this.lancamentoService.buscarPorFuncionarioId(1l, new PageRequest(1, 10));
		assertNotNull(lancamento);
	}
	
	public void testBuscarPorLancamentoId() {
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(1l);
		assertTrue(lancamento.isPresent());
	}
	
}
