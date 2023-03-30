package br.com.faculdadeimpacta.aluno.charlie.agendaplus;

import br.com.faculdadeimpacta.aluno.charlie.agendaplus.repository.ContactRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = ContactRepository.class)
public class AgendaplusApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendaplusApplication.class, args);
	}

}
