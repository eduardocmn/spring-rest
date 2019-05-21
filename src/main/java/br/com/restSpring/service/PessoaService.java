package br.com.restSpring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.restSpring.dto.PessoaDTO;
import br.com.restSpring.entidade.Pessoa;
import br.com.restSpring.repository.Repositorio;

@Service
@Component
public class PessoaService {
	
	@Autowired
	private Repositorio repositorio;
	
	
	public PessoaDTO salvar(PessoaDTO pessoaDTO) {

		Pessoa pessoa = new Pessoa();

		pessoa.setNome(pessoaDTO.getNome());
		pessoa.setLatitude(pessoaDTO.getLatitude());
		pessoa.setLongitude(pessoaDTO.getLongitude());
		pessoa.setTempMin(pessoaDTO.getTempMin());
		pessoa.setTempMax(pessoaDTO.getTempMax());
		pessoa.setIdade(pessoaDTO.getIdade());
		
		pessoa = repositorio.save(pessoa);
		
		if(pessoa != null){
			pessoaDTO.setCodigo(pessoa.getCodigo());
		}
		
		return pessoaDTO;
	}

	public PessoaDTO buscar(Long id) {
		Pessoa pessoa = repositorio.findByCodigo(id);
		PessoaDTO pessoaDTO = new PessoaDTO();
		
		if (pessoa == null) {
			System.out.println("Não existe este cliente cadastrado");
		}else{
			pessoaDTO.setCodigo(pessoa.getCodigo());
			pessoaDTO.setNome(pessoa.getNome());
			pessoaDTO.setLatitude(pessoa.getLatitude());
			pessoaDTO.setLongitude(pessoa.getLongitude());
			pessoaDTO.setTempMin(pessoa.getTempMin());
			pessoaDTO.setTempMax(pessoa.getTempMax());
			pessoaDTO.setIdade(pessoa.getIdade());
		}
		
		return pessoaDTO;
	}
	
	public PessoaDTO deletar(Long id) {
		
		Pessoa pessoa = repositorio.findByCodigo(id);
		
		PessoaDTO pessoaDTO = new PessoaDTO();
		
		if (pessoa != null) {
			pessoaDTO.setCodigo(pessoa.getCodigo());
			pessoaDTO.setNome(pessoa.getNome());
		}else if(pessoa == null || pessoa.getCodigo() == null){
			return null;
		}
		
		repositorio.delete(id);
		
		return pessoaDTO;
	}
	
}
