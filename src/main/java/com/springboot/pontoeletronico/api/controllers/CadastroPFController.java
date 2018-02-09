package com.springboot.pontoeletronico.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.pontoeletronico.api.dtos.CadastroPFDto;
import com.springboot.pontoeletronico.api.dtos.CadastroPJDto;
import com.springboot.pontoeletronico.api.entities.Empresa;
import com.springboot.pontoeletronico.api.entities.Funcionario;
import com.springboot.pontoeletronico.api.enums.PerfilEnum;
import com.springboot.pontoeletronico.api.response.Response;
import com.springboot.pontoeletronico.api.services.EmpresaService;
import com.springboot.pontoeletronico.api.services.FuncionarioService;
import com.springboot.pontoeletronico.api.utils.PassWordUtils;


@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPFController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto,
						BindingResult result) throws NoSuchAlgorithmException{
		
		log.info("Cadastro pessoa fisica {}", cadastroPFDto.toString());
		Response<CadastroPFDto> response = new Response<CadastroPFDto>();
		
		validarDadosExistentes(cadastroPFDto, result);
		//Empresa empresa = this.converterDtoParaEmpresa(cadastroPFDto);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPFDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando pf {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		
		Optional<Empresa> empresa = this.empresaService.buscaPorCnpj(cadastroPFDto.getCnpj());
		empresa.ifPresent(empr -> funcionario.setEmpresa(empr));
		this.funcionarioService.persistir(funcionario);
	
		response.setData(this.converterCadastroPFDto(funcionario));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Verifica se o funcionario e a empresa ja existe na base
	 * 
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPFDto cadastroPJDto, BindingResult result) {
		
		Optional<Empresa> empresa = this.empresaService.buscaPorCnpj(cadastroPJDto.getCnpj());
		if(!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "empresa nao existe"));
		}
		
		this.funcionarioService.buscaPorCpf(cadastroPJDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF ja existe")));
	}
	
	/**
	 * Converter DTO para empresa
	 * @param cadastroPJDto
	 * @return
	 */
	private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());
		return empresa;
	}
	
	/**
	 * Converter DTO para Funcionario
	 * @param cadastroPFDto
	 * @param result
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult result) 
				throws NoSuchAlgorithmException{
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPFDto.getNome());
		funcionario.setEmail(cadastroPFDto.getEmail());
		funcionario.setCpf(cadastroPFDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PassWordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		
		cadastroPFDto.getQtdHorasAlmoco()
			.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		cadastroPFDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));
		cadastroPFDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		return funcionario;
	}
	
	/**
	 * Popula dto CadastroPJDto
	 * @param funcionario
	 * @return
	 */
	private CadastroPFDto converterCadastroPFDto(Funcionario funcionario) {
		CadastroPFDto cadastroPFDto = new CadastroPFDto();
		cadastroPFDto.setId(funcionario.getId());
		cadastroPFDto.setNome(funcionario.getNome());
		cadastroPFDto.setEmail(funcionario.getEmail());
		cadastroPFDto.setCnpj(funcionario.getCpf());
		cadastroPFDto.setCnpj(funcionario.getEmpresa().getCnpj());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(qtdHorasAlmoco -> 
			cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabalhoDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
		funcionario.getValorHoraOpt()
			.ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));
		return cadastroPFDto;
	}
}
