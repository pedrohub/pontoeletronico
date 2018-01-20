package com.springboot.pontoeletronico.api.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.springboot.pontoeletronico.api.entities.Empresa;
import com.springboot.pontoeletronico.api.entities.Funcionario;
import com.springboot.pontoeletronico.api.entities.Lancamento;
import com.springboot.pontoeletronico.api.enums.PerfilEnum;
import com.springboot.pontoeletronico.api.enums.TipoEnum;
import com.springboot.pontoeletronico.api.utils.PassWordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	
	@Autowired
	FuncionarioRepository funcionarioRepository;
	
	@Autowired
	EmpresaRepository empresaRepository;
	
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	private Long funcionarioId;
	private static final String EMAIL = "email@email.com";
	private static final String CNPJ = "123456";
	
	
	@Before
	public void setUp() {
		Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		Funcionario funcionario = this.funcionarioRepository.save(obterDadosFuncionario(empresa));
		this.funcionarioId = funcionario.getId();
		
		this.lancamentoRepository.save(obterDadosLancamento(funcionario));
		this.lancamentoRepository.save(obterDadosLancamento(funcionario));
	}
	
	@After
	public final void tearDown() {
		this.empresaRepository.deleteAll();
	}
	
	@Test
	public void testBuscarLancamentoPorFuncionario() {
		List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);
		
		assertEquals(2, lancamentos.size());
	}
	
	@Test
	public void testBuscarLancamentoPorFuncionarioIdPaginado() {
		PageRequest page = new PageRequest(0, 10);
		Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);
		
		assertEquals(2, lancamentos.getTotalElements());
	}
	
	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("teste dessa empresa");
		empresa.setCnpj(CNPJ);
		return empresa;
	}
	
	private Funcionario obterDadosFuncionario(Empresa empresa) {
		Funcionario funcionario = new Funcionario();
		funcionario.setEmail(EMAIL);
		funcionario.setEmpresa(empresa);
		funcionario.setNome("teste da app");
		funcionario.setSenha(PassWordUtils.gerarBCrypt("1234"));
		funcionario.setCpf("123");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		return funcionario;
	}
	
	private Lancamento obterDadosLancamento(Funcionario funcionario) {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(new Date());
		lancamento.setTipo(TipoEnum.INICIO_ALMOCO);
		lancamento.setFuncionario(funcionario);
		lancamento.setDescricao("hora de almoço");
		lancamento.setLocalizacao("Rua da hora");
		return lancamento;
	}
	
}
