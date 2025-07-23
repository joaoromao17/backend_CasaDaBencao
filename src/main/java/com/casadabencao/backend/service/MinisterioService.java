package com.casadabencao.backend.service;

import com.casadabencao.backend.dto.MinisterioDto;
import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.MinisterioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class MinisterioService {

    @Autowired
    private MinisterioRepository ministerioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CloudinaryService cloudinaryService;


    public List<Ministerio> findAll() {
        return ministerioRepository.findAll();
    }

    public Optional<Ministerio> findById(Long id) {
        return ministerioRepository.findById(id);
    }

    public Ministerio save(Ministerio ministerio) {
        return ministerioRepository.save(ministerio);
    }

    public Ministerio update(Long id, Ministerio atualizado) {
        return ministerioRepository.findById(id).map(ministerio -> {
            ministerio.setName(atualizado.getName());
            ministerio.setDescription(atualizado.getDescription());
            ministerio.setLeaders(atualizado.getLeaders());
            ministerio.setViceLeaders(atualizado.getViceLeaders());
            ministerio.setImageUrl(atualizado.getImageUrl());
            ministerio.setMeetingDay(atualizado.getMeetingDay());
            ministerio.setAtividades(atualizado.getAtividades());
            ministerio.setWall(atualizado.getWall());
            return ministerioRepository.save(ministerio);
        }).orElse(null);
    }

    public void delete(Long id) {
        ministerioRepository.deleteById(id);
    }

    public List<MinisterioDto> getAllWithLeaders() {
        return ministerioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MinisterioDto convertToDto(Ministerio ministerio) {
        MinisterioDto dto = new MinisterioDto();
        dto.setId(ministerio.getId());
        dto.setName(ministerio.getName());
        dto.setDescription(ministerio.getDescription());
        dto.setImageUrl(ministerio.getImageUrl());
        dto.setWall(ministerio.getWall());
        dto.setMeetingDay(ministerio.getMeetingDay());

        dto.setLeaderIds(Optional.ofNullable(ministerio.getLeaders())
                .orElse(Collections.emptyList())
                .stream()
                .map(Usuario::getId)
                .collect(Collectors.toList()));

        dto.setLeaderNames(Optional.ofNullable(ministerio.getLeaders())
                .orElse(Collections.emptyList())
                .stream()
                .map(Usuario::getName)
                .collect(Collectors.toList()));

        dto.setViceLeaderIds(Optional.ofNullable(ministerio.getViceLeaders())
                .orElse(Collections.emptyList())
                .stream()
                .map(Usuario::getId)
                .collect(Collectors.toList()));

        dto.setViceLeaderNames(Optional.ofNullable(ministerio.getViceLeaders())
                .orElse(Collections.emptyList())
                .stream()
                .map(Usuario::getName)
                .collect(Collectors.toList()));

        dto.setActivities(Optional.ofNullable(ministerio.getAtividades())
                .orElse(Collections.emptyList()));

        return dto;
    }


    public List<Usuario> findAllById(List<Long> ids) {
        return usuarioService.findAllById(ids);
    }


    public Ministerio updateFromDto(Long id, MinisterioDto dto, MultipartFile image) {
        Ministerio ministerio = findById(id)
                .orElseThrow(() -> new RuntimeException("Ministério não encontrado"));

        if (dto.getName() != null) ministerio.setName(dto.getName());
        if (dto.getDescription() != null) ministerio.setDescription(dto.getDescription());
        if (dto.getMeetingDay() != null) ministerio.setMeetingDay(dto.getMeetingDay());

        if (dto.getLeaderIds() != null) {
            List<Usuario> leaders = usuarioService.findAllById(dto.getLeaderIds());
            ministerio.setLeaders(leaders);
        }

        if (dto.getViceLeaderIds() != null) {
            List<Usuario> viceLeaders = usuarioService.findAllById(dto.getViceLeaderIds());
            ministerio.setViceLeaders(viceLeaders);
        }

        if (dto.getActivities() != null) {
            ministerio.setAtividades(dto.getActivities());
        }

        if (image != null && !image.isEmpty() && image.getContentType().startsWith("image/")) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image, "ministerios");
                ministerio.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem no Cloudinary", e);
            }
        }

        return ministerioRepository.save(ministerio);
    }
}
