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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.pontoeletronico.api.dtos.FuncionarioDto;
import com.springboot.pontoeletronico.api.entities.Funcionario;
import com.springboot.pontoeletronico.api.response.Response;
import com.springboot.pontoeletronico.api.services.FuncionarioService;
import com.springboot.pontoeletronico.api.utils.PassWordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {
	
	private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public FuncionarioController() {
		
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{
				
		log.info("Atualizando funcionario {}", funcionarioDto.toString());
		Response<FuncionarioDto> response = new Response<FuncionarioDto>();
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscaPorId(id);
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario nao encontrado"));
		}
		
		this.atualizarDadosfuncionario(funcionario.get(), funcionarioDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando funcionario: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.funcionarioService.persistir(funcionario.get());
		response.setData(this.converterFuncionarioDto(funcionario.get()));
		
		return ResponseEntity.ok(response);
		
	}
	
	private void atualizarDadosfuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto, BindingResult result) {
		
		funcionario.setNome(funcionarioDto.getNome());
		
		if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			this.funcionarioService.buscaPorEmail(funcionarioDto.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("email", "email ja cadastrado")));
			funcionario.setEmail(funcionarioDto.getEmail());
		}
		
		funcionario.setQtdHorasAlmoco(null);
		funcionarioDto.getQtdHorasAlmoco()
				.ifPresent(qtdAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdAlmoco)));
		
		funcionario.setQtdHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdDia)));
		
		funcionario.setValorHora(null);
		funcionarioDto.getValorHora()
			.ifPresent(valorhora -> funcionario.setValorHora(new BigDecimal(valorhora)));
		
		if(funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PassWordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
		}
	}
	
	private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
		FuncionarioDto funcionarioDto = new FuncionarioDto();
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setEmail(funcionario.getEmail());
		funcionarioDto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(
						qtdDia -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdDia))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdtrabalho -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdtrabalho))));
		funcionario.getValorHoraOpt().ifPresent(
				valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));
		return funcionarioDto;
	}

}
