package com.springboot.pontoeletronico.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassWordUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PassWordUtils.class);
	
	public PassWordUtils() {
		
	}
	
	/**
	 * Gerar hash de uma String
	 * @param senha
	 * @return
	 */
	public static String gerarBCrypt(String senha) {
		if (senha == null) {
			return senha;
		}
		
		log.info("Gerando hash co bCrypt");
		BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
		return bCrypt.encode(senha);
	}

}
