package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class ContactMergeRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MergeEntry {
        private UUID uuid;
        private MergeType mergeType;
    }

    private List<MergeEntry> entries;
}
