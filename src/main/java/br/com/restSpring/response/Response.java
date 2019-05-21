package br.com.restSpring.response;

import br.com.restSpring.dto.PessoaDTO;

public class Response {
	
	private String mensagem;
	
	private PessoaDTO pessoa;
	
	public Response(){
		
	}
	
	public Response(String mensagem,PessoaDTO pessoa){
		this.mensagem = mensagem;
		this.pessoa = pessoa;
	}
	
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public PessoaDTO getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaDTO pessoa) {
		this.pessoa = pessoa;
	}
	
}
