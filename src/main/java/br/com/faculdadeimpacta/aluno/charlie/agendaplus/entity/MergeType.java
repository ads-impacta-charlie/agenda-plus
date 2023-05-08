package br.com.faculdadeimpacta.aluno.charlie.agendaplus.entity;

public enum MergeType {
    /** Merge only the {@link ContactData} for two {@link Contact} */
    ONLY_DATA,
    /** Merge the complete entities from two {@link Contact} */
    FULL,
}
