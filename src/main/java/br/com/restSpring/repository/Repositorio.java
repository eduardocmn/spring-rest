package br.com.restSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.restSpring.entidade.Pessoa;

@Repository
public interface Repositorio extends JpaRepository<Pessoa, Long>{
	
	public Pessoa findByCodigo(Long id);
	
}
