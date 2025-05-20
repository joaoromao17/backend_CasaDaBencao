package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Evento;
import com.casadabencao.backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    public List<Evento> findUpcoming() {
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        return eventoRepository.findByDateGreaterThanEqualOrderByDateAsc(inicioDoDia);
    }

    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento update(Long id, Evento eventoAtualizado) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setTitle(eventoAtualizado.getTitle());
            evento.setDescription(eventoAtualizado.getDescription());
            evento.setLocation(eventoAtualizado.getLocation());
            evento.setDate(eventoAtualizado.getDate());
            evento.setTime(eventoAtualizado.getTime());
            evento.setImageUrl(eventoAtualizado.getImageUrl());
            evento.setCategory(eventoAtualizado.getCategory());
            return eventoRepository.save(evento);
        }).orElse(null);
    }

    public void delete(Long id) {
        eventoRepository.deleteById(id);
    }
}
