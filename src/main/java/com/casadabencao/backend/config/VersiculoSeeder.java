package com.casadabencao.backend.config;

import com.casadabencao.backend.model.Versiculo;
import com.casadabencao.backend.repository.VersiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VersiculoSeeder {

    @Bean
    CommandLineRunner initVersiculos(VersiculoRepository versiculoRepository) {
        return args -> {
            if (versiculoRepository.count() == 0) {
                List<Versiculo> versiculos = List.of(
                        new Versiculo("O Senhor é o meu pastor; de nada terei falta.", "Salmos 23:1"),
                        new Versiculo("Entrega o teu caminho ao Senhor; confia nele, e ele o fará.", "Salmos 37:5"),
                        new Versiculo("Posso todas as coisas naquele que me fortalece.", "Filipenses 4:13"),
                        new Versiculo("Porque sou eu que conheço os planos que tenho para vocês, diz o Senhor.", "Jeremias 29:11"),
                        new Versiculo("O Senhor está perto de todos os que o invocam.", "Salmos 145:18"),
                        new Versiculo("Confia no Senhor de todo o teu coração e não te apoies no teu próprio entendimento.", "Provérbios 3:5"),
                        new Versiculo("Tudo posso naquele que me fortalece.", "Filipenses 4:13"),
                        new Versiculo("Alegrem-se na esperança, sejam pacientes na tribulação, perseverem na oração.", "Romanos 12:12"),
                        new Versiculo("O choro pode durar uma noite, mas a alegria vem pela manhã.", "Salmos 30:5"),
                        new Versiculo("A fé é a certeza daquilo que esperamos e a prova das coisas que não vemos.", "Hebreus 11:1"),
                        new Versiculo("Não temas, porque eu sou contigo.", "Isaías 41:10"),
                        new Versiculo("O meu mandamento é este: amem-se uns aos outros como eu os amei.", "João 15:12"),
                        new Versiculo("Deem graças em todas as circunstâncias, pois esta é a vontade de Deus para vocês.", "1 Tessalonicenses 5:18"),
                        new Versiculo("Deus é o nosso refúgio e fortaleza, socorro bem presente na angústia.", "Salmos 46:1"),
                        new Versiculo("O Senhor é bom, um refúgio em tempos de angústia.", "Naum 1:7"),
                        new Versiculo("Venham a mim, todos os que estão cansados e sobrecarregados, e eu lhes darei descanso.", "Mateus 11:28"),
                        new Versiculo("O Senhor lutará por vocês; tão somente acalmem-se.", "Êxodo 14:14"),
                        new Versiculo("Não se turbe o vosso coração; credes em Deus, crede também em mim.", "João 14:1"),
                        new Versiculo("Busquem em primeiro lugar o Reino de Deus e a sua justiça, e todas estas coisas lhes serão acrescentadas.", "Mateus 6:33"),
                        new Versiculo("Bem-aventurados os que choram, pois serão consolados.", "Mateus 5:4"),
                        new Versiculo("O justo viverá pela fé.", "Romanos 1:17"),
                        new Versiculo("Não se deixem vencer pelo mal, mas vençam o mal com o bem.", "Romanos 12:21"),
                        new Versiculo("Sede fortes e corajosos. Não temais.", "Deuteronômio 31:6"),
                        new Versiculo("Em tudo dai graças.", "1 Tessalonicenses 5:18"),
                        new Versiculo("A resposta branda desvia o furor.", "Provérbios 15:1"),
                        new Versiculo("Crê no Senhor Jesus e será salvo, tu e tua casa.", "Atos 16:31"),
                        new Versiculo("Tudo tem o seu tempo determinado.", "Eclesiastes 3:1"),
                        new Versiculo("O amor é paciente, o amor é bondoso.", "1 Coríntios 13:4"),
                        new Versiculo("A tua palavra é lâmpada para os meus pés e luz para o meu caminho.", "Salmos 119:105"),
                        new Versiculo("Quem habita no esconderijo do Altíssimo e descansa à sombra do Onipotente.", "Salmos 91:1")
                );

                versiculoRepository.saveAll(versiculos);
                System.out.println("🌱 Versículos inseridos no banco com sucesso!");
            } else {
                System.out.println("✅ Versículos já estão presentes no banco.");
            }
        };
    }
}
