package com.springboot.pontoeletronico.api.controllers;

import java.security.NoSuchAlgorithmException;

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

import com.springboot.pontoeletronico.api.dtos.CadastroPJDto;
import com.springboot.pontoeletronico.api.entities.Empresa;
import com.springboot.pontoeletronico.api.entities.Funcionario;
import com.springboot.pontoeletronico.api.enums.PerfilEnum;
import com.springboot.pontoeletronico.api.response.Response;
import com.springboot.pontoeletronico.api.services.EmpresaService;
import com.springboot.pontoeletronico.api.services.FuncionarioService;
import com.springboot.pontoeletronico.api.utils.PassWordUtils;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPJController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
						BindingResult result) throws NoSuchAlgorithmException{
		
		log.info("Cadastro pessoa juridica {}", cadastroPJDto.toString());
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();
		
		validarDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando pj {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPJDto(funcionario));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Verifica se o funcionario e a empresa ja existe na base
	 * 
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		
		this.empresaService.buscaPorCnpj(cadastroPJDto.getCnpj())
			.ifPresent(emp -> result.addError(new ObjectError("empresa", "empresa ja existe")));
		
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
	 * @param cadastroPJDto
	 * @param result
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) 
				throws NoSuchAlgorithmException{
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDto.getNome());
		funcionario.setEmail(cadastroPJDto.getEmail());
		funcionario.setCpf(cadastroPJDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PassWordUtils.gerarBCrypt(cadastroPJDto.getSenha()));
		return funcionario;
	}
	
	/**
	 * Popula dto CadastroPJDto
	 * @param funcionario
	 * @return
	 */
	private CadastroPJDto converterCadastroPJDto(Funcionario funcionario) {
		CadastroPJDto cadastroPJDto = new CadastroPJDto();
		cadastroPJDto.setId(funcionario.getId());
		cadastroPJDto.setNome(funcionario.getNome());
		cadastroPJDto.setEmail(funcionario.getEmail());
		cadastroPJDto.setCpf(funcionario.getCpf());
		cadastroPJDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());
		return cadastroPJDto;
	}
}
